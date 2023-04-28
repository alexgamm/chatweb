package chatweb;

import chatweb.db.Database;
import chatweb.endpoint.*;
import chatweb.longpoll.LongPollFuture;
import chatweb.model.Message;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.MessageService;
import chatweb.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
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
        UserRepository userRepository = new UserRepository(database);
        SessionRepository sessionRepository = new SessionRepository(database);
        TemplateLoader templateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars(templateLoader);
        MessageService messageService = new MessageService();

        webServer.addEndpoint("/", new IndexEndpoint(userRepository, sessionRepository, handlebars));
        webServer.addEndpoint("/login", new LoginEndpoint(handlebars, userRepository, sessionRepository));
        webServer.addEndpoint("/registration", new RegistrationEndpoint(handlebars, userRepository, sessionRepository));
        webServer.addEndpoint("/api/users", new UsersEndpoint(userRepository));
        webServer.addEndpoint("/api/messages", new MessagesEndpoint(userRepository, sessionRepository, messageService));
    }
}