package chatweb.service;

import chatweb.longpoll.LongPollFuture;
import chatweb.model.event.Event;
import chatweb.model.event.UserOnlineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class EventsService {
    private static final long LONG_POLL_TIMEOUT = 20_000;
    private final List<Event> events = Collections.synchronizedList(new LinkedList<>());
    private final Set<LongPollFuture> longPollFutures = Collections.synchronizedSet(new HashSet<>());
    private final Map<Integer, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void removeExpiredLongPollFutures() {
        Set<LongPollFuture> expiredLongPollFutures = longPollFutures.stream()
                .filter(future -> System.currentTimeMillis() - future.getCreatedAt() >= LONG_POLL_TIMEOUT)
                .collect(Collectors.toSet());
        longPollFutures.removeAll(expiredLongPollFutures);
        expiredLongPollFutures.forEach(future -> future.complete(Collections.emptyList()));
    }

    public void addEvent(Event event) {
        addEvent((userId) -> event);
    }

    public void addEvent(Function<Integer, Event> eventSupplier) {
        events.add(eventSupplier.apply(null));
        Set<LongPollFuture> currentLongPollFutures = new HashSet<>(longPollFutures);
        longPollFutures.clear();
        currentLongPollFutures.stream().forEach(future -> future.complete(getEvents(future.getTs())));

        emitters.forEach((userId, userEmitters) -> {
            if (userEmitters.isEmpty()) {
                return;
            }
            Event event = eventSupplier.apply(userId);
            userEmitters.forEach(emitter -> {
                SseEmitter.SseEventBuilder builder = SseEmitter.event()
                        .data(event)
                        .id(UUID.randomUUID().toString())
                        .name(event.getType().name());
                try {
                    emitter.send(builder);
                } catch (Throwable ignored) {
                }
            });
        });
    }

    public void addLongPollFuture(LongPollFuture longPollFuture) {
        longPollFutures.add(longPollFuture);
    }

    public SseEmitter createEmitter(int userId) {
        SseEmitter emitter = new SseEmitter();
        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));
        emitters.compute(userId, (key, value) -> { // лямда должна вернуть новое значение для этого ключа
            if (value == null) {
                value = new HashSet<>();
            }
            value.add(emitter);
            return value;
        });
        if (emitters.getOrDefault(userId, Collections.emptySet()).size() == 1) {
            addEvent(new UserOnlineEvent(userId, true));
        }
        return emitter;
    }

    private void removeEmitter(int userId, SseEmitter emitter) {
        emitters.computeIfPresent(userId, (key, value) -> {
            value.remove(emitter);
            return value;
        });
        emitters.values().removeIf(Set::isEmpty);
        if (!hasEmitters(userId)) {
            addEvent(new UserOnlineEvent(userId, false));
        }
    }

    public boolean hasEmitters(int userId) {
        return !emitters.getOrDefault(userId, Collections.emptySet()).isEmpty();
    }

    public List<Event> getEvents(long ts) {
        return events.stream()
                .filter(event -> event.getDate().getTime() > ts)
                .collect(Collectors.toList());
    }

    public List<Event> getEvents() {
        return events;
    }
}
