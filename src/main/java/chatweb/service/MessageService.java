package chatweb.service;

import chatweb.longpoll.LongPollFuture;
import chatweb.model.Message;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MessageService {
    private static final long LONG_POLL_TIMEOUT = 20_000;
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final List<Message> messages = new ArrayList<>();
    private final Set<LongPollFuture> longPollFutures = new HashSet<>();

    public MessageService() {
        SCHEDULER.scheduleWithFixedDelay(() -> {
            Set<LongPollFuture> expiredLongPollFutures = longPollFutures.stream()
                    .filter(future -> System.currentTimeMillis() - future.getCreatedAt() >= LONG_POLL_TIMEOUT)
                    .collect(Collectors.toSet());
            longPollFutures.removeAll(expiredLongPollFutures);
            expiredLongPollFutures.forEach(future -> future.complete(Collections.emptyList()));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void addMessage(Message message) {
        messages.add(message);
        Set<LongPollFuture> currentLongPollFutures = new HashSet<>(longPollFutures);
        longPollFutures.clear();
        currentLongPollFutures.stream().forEach(future -> future.complete(getNewMessages(future.getTs())));
    }

    public void addLongPollFuture(LongPollFuture longPollFuture) {
        longPollFutures.add(longPollFuture);
    }

    public List<Message> getNewMessages(long ts) {
        return messages.stream()
                .filter(message -> message.getSendDate().getTime() > ts)
                .collect(Collectors.toList());
    }
}
