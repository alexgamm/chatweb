package chatweb.service;

import chatweb.longpoll.LongPollFuture;
import chatweb.model.event.Event;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class EventsService {
    private static final long LONG_POLL_TIMEOUT = 20_000;
    private final List<Event> events = Collections.synchronizedList(new LinkedList<>());
    private final Set<LongPollFuture> longPollFutures = Collections.synchronizedSet(new HashSet<>());

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void removeExpiredLongPollFutures() {
        Set<LongPollFuture> expiredLongPollFutures = longPollFutures.stream()
                .filter(future -> System.currentTimeMillis() - future.getCreatedAt() >= LONG_POLL_TIMEOUT)
                .collect(Collectors.toSet());
        longPollFutures.removeAll(expiredLongPollFutures);
        expiredLongPollFutures.forEach(future -> future.complete(Collections.emptyList()));
    }

    public void addEvent(Event event) {
        events.add(event);
        Set<LongPollFuture> currentLongPollFutures = new HashSet<>(longPollFutures);
        longPollFutures.clear();
        currentLongPollFutures.stream().forEach(future -> future.complete(getEvents(future.getTs())));
    }

    public void addLongPollFuture(LongPollFuture longPollFuture) {
        longPollFutures.add(longPollFuture);
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
