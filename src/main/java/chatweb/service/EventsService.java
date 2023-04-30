package chatweb.service;

import chatweb.longpoll.LongPollFuture;
import chatweb.model.Event;
import chatweb.model.Message;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EventsService {
    private static final long LONG_POLL_TIMEOUT = 20_000;
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final List<Event> events = new ArrayList<>();
    private final Set<LongPollFuture> longPollFutures = new HashSet<>();

    public EventsService() {
        SCHEDULER.scheduleWithFixedDelay(() -> {
            Set<LongPollFuture> expiredLongPollFutures = longPollFutures.stream()
                    .filter(future -> System.currentTimeMillis() - future.getCreatedAt() >= LONG_POLL_TIMEOUT)
                    .collect(Collectors.toSet());
            longPollFutures.removeAll(expiredLongPollFutures);
            expiredLongPollFutures.forEach(future -> future.complete(Collections.emptyList()));
        }, 0, 1, TimeUnit.SECONDS);
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
}
