package webserver;

import chatweb.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class Request {
    private final HttpExchange exchange;
    private final ObjectMapper objectMapper;
    private Object body;


    public Request(HttpExchange exchange, ObjectMapper objectMapper) {
        this.exchange = exchange;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getQuery() {
        return HttpUtils.parseQueryString(exchange.getRequestURI().getQuery());
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getBody() {
        if (body == null) {
            body = HttpUtils.parseQueryString(HttpUtils.readRequestBody(exchange));
        }
        return (Map<String, String>) body;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> type) throws RequestFailedException {
        if (body == null) {
            try {
                body = objectMapper.readValue(HttpUtils.readRequestBody(exchange), type);
            } catch (JsonProcessingException e) {
                throw new RequestFailedException(400, e.getMessage());
            }
        }
        return (T) body;
    }

    public Map<String, String> getCookies() {
        return HttpUtils.getCookies(exchange);
    }
}
