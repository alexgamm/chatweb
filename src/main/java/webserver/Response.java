package webserver;

import java.util.Collections;
import java.util.HashMap;
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
        return redirect(location, Collections.emptyMap());
    }

    public static Response redirect(String location, Map<String, String> headers) {
        Map<String, String> allHeaders = new HashMap<>(headers);
        allHeaders.put("Location", location);
        return new Response(allHeaders, 302, "");
    }
    public static Response badRequest(String body){
        return new Response(Collections.emptyMap(), 400, body);
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
