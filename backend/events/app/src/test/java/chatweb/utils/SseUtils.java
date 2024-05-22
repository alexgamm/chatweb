package chatweb.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.Arrays;
import java.util.List;

public class SseUtils {

    public static List<DocumentContext> parseResponse(String response) {
        return Arrays.stream(response.split("\n"))
                .filter(line -> line.startsWith("data:"))
                .map(line -> line.substring(5))
                .map(JsonPath::parse)
                .toList();
    }
}
