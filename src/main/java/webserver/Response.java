package webserver;

import java.util.Collections;
import java.util.Map;

public class Response {
    private final Map<String, String> headers;
    private final int statusCode;
    private final String body;

    public Response(Map<String, String> headers, int statusCode, String body) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.body = body;
    }

    public static Response redirect(String location) {
        return new Response(Collections.singletonMap("Location", location), 302, "");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
