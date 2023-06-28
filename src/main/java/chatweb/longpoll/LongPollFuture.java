package chatweb.longpoll;

import chatweb.model.event.Event;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LongPollFuture extends CompletableFuture<List<Event>> {
    private final long ts;
    private final long createdAt = System.currentTimeMillis();

    public LongPollFuture(long ts) {
        this.ts = ts;
    }

    public long getTs() {
        return ts;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}
