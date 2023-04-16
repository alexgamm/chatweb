package chatweb.service;

import chatweb.longpoll.LongPollHandler;
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
    private final Set<LongPollHandler> longPollHandlers = new HashSet<>();

    public MessageService() {
        SCHEDULER.scheduleWithFixedDelay(() -> {
            Set<LongPollHandler> expiredLongPollHandlers = longPollHandlers.stream()
                    .filter(handler -> System.currentTimeMillis() - handler.getCreatedAt() >= LONG_POLL_TIMEOUT)
                    .collect(Collectors.toSet());
            longPollHandlers.removeAll(expiredLongPollHandlers);
            expiredLongPollHandlers.forEach(handler -> handler.handle(Collections.emptyList()));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void addMessage(Message message) {
        messages.add(message);
        Set<LongPollHandler> currentLongPollHandlers = new HashSet<>(longPollHandlers);
        longPollHandlers.clear();
        currentLongPollHandlers.stream().forEach(handler -> handler.handle(getNewMessages(handler.getTs())));
    }

    public void addLongPollHandler(LongPollHandler longPollHandler) {
        longPollHandlers.add(longPollHandler);
    }

    public List<Message> getNewMessages(long ts) {
        return messages.stream()
                .filter(message -> message.getSendDate().getTime() > ts)
                .collect(Collectors.toList());
    }
}
