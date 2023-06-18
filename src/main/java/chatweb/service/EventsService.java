package chatweb.service;

import chatweb.longpoll.LongPollFuture;
import chatweb.model.Event;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EventsService {
    private static final long LONG_POLL_TIMEOUT = 20_000;
    //TODO use spring scheduler
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final List<Event> events = Collections.synchronizedList(new LinkedList<>());
    private final Set<LongPollFuture> longPollFutures = Collections.synchronizedSet(new HashSet<>());

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

    public List<Event> getEvents() {
        return events;
    }
}
