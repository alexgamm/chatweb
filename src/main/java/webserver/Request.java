package webserver;

import chatweb.utils.HttpUtils;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class Request {
    private final HttpExchange exchange;
    private Object body;

    public Request(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getBody() {
        if(body == null) {
            body = HttpUtils.parseQueryString(HttpUtils.readRequestBody(exchange));
        }
        return (Map<String, String>) body;
    }

    public Map<String, String> getCookies() {
        return HttpUtils.getCookies(exchange);
    }
}
