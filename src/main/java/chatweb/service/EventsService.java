package chatweb.service;

import chatweb.model.event.Event;
import chatweb.model.event.UserOnlineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class EventsService {
    private final Map<Integer, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEvent(Event event) {
        addEvent((userId) -> event);
    }

    public void addEvent(Function<Integer, Event> eventSupplier) {
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

}
