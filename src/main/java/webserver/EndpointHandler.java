package webserver;

import chatweb.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class EndpointHandler implements HttpHandler {
    private final static Logger LOG = LoggerFactory.getLogger(EndpointHandler.class);
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
            LOG.error("Failed to respond.", e);
        }
    }

    private void respond(HttpExchange exchange, Object response) {
        if (response instanceof Response) {
            ((Response) response).getHeaders().forEach((key, value) -> exchange.getResponseHeaders().add(key, value));
            respond(exchange, ((Response) response).getStatusCode(), ((Response) response).getBody());
        } else {
            if (!(response instanceof String)) {
                try {
                    response = objectMapper.writeValueAsString(response);
                } catch (JsonProcessingException e) {
                    respond(exchange, 500, e.getMessage());
                    return;
                }
            }
            respond(exchange, 200, (String) response);
        }
    }

    @Override
    public void handle(HttpExchange exchange) {
        Request request = new Request(exchange, objectMapper);
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
            if (response instanceof CompletableFuture<?>) {
                // обрабатывает результат выполнения future, результаты выполнения future - result, ex
                ((CompletableFuture<?>) response).whenComplete((result, ex) -> {
                    if (ex == null) {
                        respond(exchange, result);
                    } else if (ex instanceof RequestFailedException) {
                        respond(
                                exchange,
                                ((RequestFailedException) ex).getStatusCode(),
                                ((RequestFailedException) ex).getDescription()
                        );
                    } else {
                        respond(exchange, 500, ex.getMessage());
                    }
                });
            } else {
                respond(exchange, response);
            }
        } catch (RequestFailedException e) {
            respond(exchange, e.getStatusCode(), e.getDescription());
        } catch (Throwable ex) {
            respond(exchange, 500, ex.getMessage());
        }
    }
}
