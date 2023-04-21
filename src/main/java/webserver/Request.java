package webserver;

import chatweb.utils.HttpUtils;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class Request {
    private final HttpExchange exchange;

    public Request(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Map<String, String> getCookies() {
        return HttpUtils.getCookies(exchange);
    }
}
