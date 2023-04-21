package chatweb;

import chatweb.db.Database;
import chatweb.endpoint.IndexEndpoint;
import chatweb.endpoint.UsersEndpoint;
import chatweb.entity.Session;
import chatweb.entity.User;
import chatweb.longpoll.LongPollHandler;
import chatweb.model.Message;
import chatweb.model.SendMessageRequest;
import chatweb.model.UserListResponse;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.MessageService;
import chatweb.utils.HttpUtils;
import chatweb.utils.PasswordUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.sun.net.httpserver.HttpServer;
import webserver.WebServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Database database = new Database(DriverManager.getConnection(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        ));
        ObjectMapper objectMapper = new ObjectMapper();
        WebServer webServer = new WebServer(new InetSocketAddress("0.0.0.0", 80), objectMapper);
        HttpServer httpServer = webServer.getHttpServer();
        UserRepository userRepository = new UserRepository(database);
        SessionRepository sessionRepository = new SessionRepository(database);
        TemplateLoader templateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars(templateLoader);
        MessageService messageService = new MessageService();

        webServer.addEndpoint("/", new IndexEndpoint(handlebars, sessionRepository, userRepository));
        httpServer.createContext("/login", exchange -> {
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    Map<String, String> body = HttpUtils.parseQueryString(HttpUtils.readRequestBody(exchange));
                    String username = body.get("username");
                    User user = userRepository.findUserByUsername(username);
                    String password = body.get("password");
                    if (user == null || password == null || !PasswordUtils.check(password, user.getPassword())) {
                        Template template = handlebars.compile("login");
                        Map<String, Object> ctx = new HashMap<>();
                        ctx.put("username", username);
                        ctx.put("error", true);
                        HttpUtils.respond(exchange, 400, template.apply(ctx));
                        return;
                    }
                    Session session = new Session(UUID.randomUUID().toString(), user.getId());
                    sessionRepository.saveSession(session);
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + session.getId());
                    exchange.sendResponseHeaders(302, 0);

                } else {
                    Template template = handlebars.compile("login");
                    HttpUtils.respond(exchange, 200, template.apply(null));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        httpServer.createContext("/registration", exchange -> {
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    Map<String, String> body = HttpUtils.parseQueryString(HttpUtils.readRequestBody(exchange));
                    String username = body.get("username");
                    String password = body.get("password");
                    if (username == null || username.isEmpty()) {
                        Template template = handlebars.compile("registration");
                        Map<String, Object> ctx = new HashMap<>();
                        ctx.put("error", "username is missing");
                        HttpUtils.respond(exchange, 400, template.apply(ctx));
                        return;
                    }
                    if (password == null || password.length() < 6) {
                        Template template = handlebars.compile("registration");
                        Map<String, Object> ctx = new HashMap<>();
                        ctx.put("username", username);
                        ctx.put("error", "password is missing or short");
                        HttpUtils.respond(exchange, 400, template.apply(ctx));
                        return;
                    }

                    User user = userRepository.findUserByUsername(username);

                    if (user != null) {

                        Template template = handlebars.compile("registration");
                        Map<String, Object> ctx = new HashMap<>();
                        ctx.put("username", username);
                        ctx.put("error", "username has been already taken");
                        HttpUtils.respond(exchange, 400, template.apply(ctx));
                        return;
                    }

                    user = new User(0, username.toLowerCase(), PasswordUtils.hash(password), new Date());
                    userRepository.saveUser(user);
                    user = userRepository.findUserByUsername(user.getUsername());
                    Session session = new Session(UUID.randomUUID().toString(), user.getId());
                    sessionRepository.saveSession(session);
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + session.getId());
                    exchange.sendResponseHeaders(302, 0);

                } else {
                    Template template = handlebars.compile("registration");
                    HttpUtils.respond(exchange, 200, template.apply(null));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        webServer.addEndpoint("/api/users", new UsersEndpoint(userRepository));
        httpServer.createContext("/api/messages", exchange -> {
            User user = Optional.ofNullable(HttpUtils.getCookies(exchange).get("sessionId"))
                    // с чем работаем -> что возвращаем
                    .map(sessionId -> sessionRepository.findSessionById(sessionId))
                    .map(session -> userRepository.findUserById(session.getUserId()))
                    .orElse(null);
            if (user == null) {
                HttpUtils.respond(exchange, 401, "");
                return;
            }

            if (exchange.getRequestMethod().equals("GET")) {
                String tsRaw = HttpUtils.parseQueryString(exchange.getRequestURI().getQuery()).get("ts");
                long ts = Long.parseLong(tsRaw);
                List<Message> newMessages = messageService.getNewMessages(ts);
                LongPollHandler longPollHandler = new LongPollHandler(objectMapper, exchange, ts);
                if (newMessages.isEmpty()) {
                    messageService.addLongPollHandler(longPollHandler);
                } else {
                    longPollHandler.handle(newMessages);
                }
                userRepository.updateLastActivityAt(user.getId());
            } else if (exchange.getRequestMethod().equals("POST")) {
                SendMessageRequest request;
                try {
                    String requestBody = HttpUtils.readRequestBody(exchange);
                    request = objectMapper.readValue(requestBody, SendMessageRequest.class);
                } catch (Throwable ex) {
                    HttpUtils.respond(exchange, 400, "");
                    return;
                }
                if (request.getMessage() == null || request.getMessage().isBlank()) {
                    HttpUtils.respond(exchange, 400, "");
                    return;
                }
                messageService.addMessage(new Message(request.getMessage(), user.getUsername(), new Date()));
                HttpUtils.respond(exchange, 200, "");
            } else {
                HttpUtils.respond(exchange, 405, "");
            }
        });
    }
}