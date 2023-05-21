package chatweb.endpoint;

import chatweb.entity.Session;
import chatweb.entity.User;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import chatweb.utils.PasswordUtils;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginEndpoint implements Endpoint {
    private final Handlebars handlebars;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public LoginEndpoint(Handlebars handlebars, UserRepository userRepository, SessionRepository sessionRepository) {
        this.handlebars = handlebars;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        return new TemplateResponse.Builder(handlebars, "login").build();
    }


    @Override
    public Object post(Request request) throws RequestFailedException {
        String username = request.getBody().get("username");
        String password = request.getBody().get("password");
        User user = userRepository.findUserByUsername(username);
        if (user == null || password == null || !PasswordUtils.check(password, user.getPassword())) {
            return new TemplateResponse.Builder(handlebars, "login")
                    .statusCode(400)
                    .addToContext("username", username)
                    .addToContext("error", true).build();
        }
        Session session = new Session(UUID.randomUUID().toString(), user.getId());
        sessionRepository.saveSession(session);
        return Response.redirect(
                "/",
                Collections.singletonMap("Set-Cookie", "sessionId=" + session.getId())
        );
    }

}