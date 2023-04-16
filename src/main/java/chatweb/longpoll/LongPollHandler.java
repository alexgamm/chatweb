package chatweb.longpoll;

import chatweb.model.Message;
import chatweb.model.MessagesResponse;
import chatweb.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class LongPollHandler {
    private final ObjectMapper objectMapper;
    private final HttpExchange exchange;
    private final long ts;
    private final long createdAt = System.currentTimeMillis();

    public LongPollHandler(ObjectMapper objectMapper, HttpExchange exchange, long ts) {
        this.objectMapper = objectMapper;
        this.exchange = exchange;
        this.ts = ts;
    }

    public long getTs() {
        return ts;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void handle(List<Message> messages) {
        MessagesResponse messagesResponse = new MessagesResponse(messages);
        try {
            HttpUtils.respond(exchange, 200, objectMapper.writeValueAsString(messagesResponse));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
