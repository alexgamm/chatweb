package webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private final static Logger LOG = LoggerFactory.getLogger(WebServer.class);
    private final static ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private final HttpServer httpServer;
    private final ObjectMapper objectMapper;

    public WebServer(InetSocketAddress address, ObjectMapper objectMapper) throws IOException {
        this.httpServer = HttpServer.create(address, 0);
        this.httpServer.setExecutor(EXECUTOR);
        this.httpServer.start();
        this.objectMapper = objectMapper;
    }

    public void addEndpoint(String path, Endpoint endpoint) {
        httpServer.createContext(path, new EndpointHandler(endpoint, objectMapper));
        LOG.info("{} added to {}", endpoint.getClass().getName(), path);
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }
}
