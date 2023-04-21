package webserver;

import chatweb.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class EndpointHandler implements HttpHandler {
    private final Endpoint endpoint;
    private final ObjectMapper objectMapper;

    public EndpointHandler(Endpoint endpoint, ObjectMapper objectMapper) {
        this.endpoint = endpoint;
        this.objectMapper = objectMapper;
    }

    private static void respond(HttpExchange exchange, int statusCode, String response) {
        try {
            HttpUtils.respond(exchange, statusCode, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
            // TODO log error
        }
    }

    @Override
    public void handle(HttpExchange exchange) {
        Request request = new Request(exchange);
        Object response;
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    response = endpoint.get(request);
                    break;
                case "POST":
                    response = endpoint.post(request);
                    break;
                default:
                    throw new RequestFailedException(405, "Method not allowed");
            }
            if (response instanceof Response) {
                ((Response) response).getHeaders().forEach((key, value) -> exchange.getResponseHeaders().add(key, value));
                respond(exchange, ((Response) response).getStatusCode(), ((Response) response).getBody());
            } else {
                if (!(response instanceof String)){
                    response = objectMapper.writeValueAsString(response);
                }
                respond(exchange, 200, (String) response);
            }
        } catch (RequestFailedException e) {
            respond(exchange, e.getStatusCode(), e.getDescription());
        } catch (Throwable ex) {
            respond(exchange, 500, ex.getMessage());
        }
    }
}
