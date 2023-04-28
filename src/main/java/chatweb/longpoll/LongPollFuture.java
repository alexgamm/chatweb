package chatweb.longpoll;

import chatweb.model.Message;
import chatweb.model.MessagesResponse;
import chatweb.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LongPollFuture extends CompletableFuture<List<Message>> {
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
