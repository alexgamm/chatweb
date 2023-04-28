package chatweb.endpoint;

import chatweb.entity.Session;
import chatweb.entity.User;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import chatweb.utils.PasswordUtils;
import com.github.jknack.handlebars.Handlebars;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class RegistrationEndpoint implements Endpoint {
    private static final String TEMPLATE_NAME = "registration";
    private final Handlebars handlebars;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public RegistrationEndpoint(Handlebars handlebars, UserRepository userRepository, SessionRepository sessionRepository) {
        this.handlebars = handlebars;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME).build();
    }

    @Override
    public Object post(Request request) throws RequestFailedException {
        String username = request.getBody().get("username");
        String password = request.getBody().get("password");
        if (username == null || username.isEmpty()) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("error", "username is missing")
                    .build();
        }
        if (password == null || password.length() < 6) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("error", "password is missing or short")
                    .addToContext("username", username)
                    .build();
        }
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("username", username)
                    .addToContext("error", "username has been already taken")
                    .build();
        }
        user = new User(0, username.toLowerCase(), PasswordUtils.hash(password), new Date());
        userRepository.saveUser(user);
        user = userRepository.findUserByUsername(user.getUsername());
        Session session = new Session(UUID.randomUUID().toString(), user.getId());
        sessionRepository.saveSession(session);
        return Response.redirect(
                "/",
                Collections.singletonMap("Set-Cookie", "sessionId=" + session.getId())
        );
    }
}
