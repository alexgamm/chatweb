package webserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Request {
    private final HttpExchange exchange;
    private final ObjectMapper objectMapper;
    private Object body;


    public Request(HttpExchange exchange, ObjectMapper objectMapper) {
        this.exchange = exchange;
        this.objectMapper = objectMapper;
    }

    public static String readRequestBody(HttpExchange exchange) {
        Scanner scanner = new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : null;
    }

    public static Map<String, String> parseQueryString(String queryString) {
        if (queryString == null) {
            return Collections.emptyMap();
        }
        Map<String, String> query = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length < 2) continue;
            String key = keyValue[0];
            String value = keyValue[1];
            query.put(key, value);
        }
        return query;
    }

    public Map<String, String> getQuery() {
        return parseQueryString(exchange.getRequestURI().getQuery());
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getBody() {
        if (body == null) {
            body = parseQueryString(readRequestBody(exchange));
        }
        return (Map<String, String>) body;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> type) throws RequestFailedException {
        if (body == null) {
            try {
                body = objectMapper.readValue(readRequestBody(exchange), type);
            } catch (JsonProcessingException e) {
                throw new RequestFailedException(400, e.getMessage());
            }
        }
        return (T) body;
    }

    public Map<String, String> getCookies() {
        String cookie = exchange.getRequestHeaders().getFirst("cookie");
        if (cookie == null) {
            return Collections.emptyMap();
        }
        Map<String, String> cookieMap = new HashMap<>();
        String[] pairs = cookie.split("; ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length < 2) continue;
            String key = keyValue[0];
            String value = keyValue[1];
            cookieMap.put(key, value);
        }
        return cookieMap;
    }
}

