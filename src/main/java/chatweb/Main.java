package chatweb;

import chatweb.db.Database;
import chatweb.endpoint.*;
import chatweb.repository.MessageRepository;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException, SQLException {
        Database database = new Database(DriverManager.getConnection(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        ));
        LOG.info("Database initialized.");
        ObjectMapper objectMapper = new ObjectMapper();
        WebServer webServer = new WebServer(new InetSocketAddress("0.0.0.0", 80), objectMapper);
        LOG.info("WebServer initialized.");
        UserRepository userRepository = new UserRepository(database);
        SessionRepository sessionRepository = new SessionRepository(database);
        MessageRepository messageRepository = new MessageRepository(database);
        TemplateLoader templateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars(templateLoader);
        EventsService eventsService = new EventsService();

        webServer.addEndpoint("/", new IndexEndpoint(userRepository, sessionRepository, handlebars));
        webServer.addEndpoint("/login", new LoginEndpoint(handlebars, userRepository, sessionRepository));
        webServer.addEndpoint("/registration", new RegistrationEndpoint(handlebars, userRepository, sessionRepository));
        webServer.addEndpoint("/api/users", new UsersEndpoint(userRepository));
        webServer.addEndpoint("/api/messages", new MessagesEndpoint(userRepository, sessionRepository, eventsService, messageRepository));
        webServer.addEndpoint("/api/events", new EventsEndpoint(userRepository, sessionRepository, eventsService));
    }
}