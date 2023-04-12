package chatweb;

import chatweb.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.sun.net.httpserver.HttpServer;
import chatweb.db.Database;
import chatweb.entity.Session;
import chatweb.entity.User;
import chatweb.model.UserListResponse;
import chatweb.repository.SessionRepository;
import chatweb.utils.HttpUtils;
import chatweb.utils.PasswordUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Database database = new Database(DriverManager.getConnection(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        ));
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 80), 0);
        UserRepository userRepository = new UserRepository(database);
        SessionRepository sessionRepository = new SessionRepository();
        TemplateLoader templateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars(templateLoader);
        ObjectMapper objectMapper = new ObjectMapper();

        httpServer.createContext("/", exchange -> {
            try {

                User user = Optional.ofNullable(HttpUtils.getCookies(exchange).get("sessionId"))
                        // с чем работаем -> что возвращаем
                        .map(sessionId -> sessionRepository.findSessionById(sessionId))
                        .map(session -> userRepository.findUserById(session.getUserId()))
                        .orElse(null);

                if (user == null) {
                    exchange.getResponseHeaders().add("Location", "/login");
                    exchange.sendResponseHeaders(302, 0);

                } else {
                    Template template = handlebars.compile("index");
                    Map<String, Object> ctx = new HashMap<>();
                    ctx.put("username", user.getUsername());
                    HttpUtils.respond(exchange, 200, template.apply(ctx));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

                    user = new User(0, username.toLowerCase(), PasswordUtils.hash(password));
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
        httpServer.createContext("/api/users", exchange -> {
            List<UserListResponse.User> list = new ArrayList<>();
            list.add(new UserListResponse.User("Luxor", new Date()));
            list.add(new UserListResponse.User("Ed Po", new Date(0)));
            UserListResponse response = new UserListResponse(list);
            HttpUtils.respond(exchange, 200, objectMapper.writeValueAsString(response));
        });
        httpServer.setExecutor(Executors.newSingleThreadExecutor());
        httpServer.start();
    }
}