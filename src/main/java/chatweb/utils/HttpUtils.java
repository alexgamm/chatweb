package chatweb.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HttpUtils {

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

    public static void respond(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        PrintWriter printWriter = new PrintWriter(exchange.getResponseBody());
        printWriter.print(response);
        printWriter.flush();
        printWriter.close();
    }

    public static String readRequestBody(HttpExchange exchange) {
        Scanner scanner = new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : null;
    }

    public static Map<String, String> getCookies(HttpExchange exchange) {
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
