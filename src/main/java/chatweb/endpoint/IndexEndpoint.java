package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.io.IOException;
import java.util.Optional;

public class IndexEndpoint implements Endpoint {

    private final Handlebars handlebars;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public IndexEndpoint(Handlebars handlebars, SessionRepository sessionRepository, UserRepository userRepository) {
        this.handlebars = handlebars;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        User user = Optional.ofNullable(request.getCookies().get("sessionId"))
                // с чем работаем -> что возвращаем
                .map(sessionId -> sessionRepository.findSessionById(sessionId))
                .map(session -> userRepository.findUserById(session.getUserId()))
                .orElse(null);
        if (user == null) {
            return Response.redirect("/login");
        } else {
            try {
                Template template = handlebars.compile("index");
                return template.apply(null);
            } catch (IOException e) {
                throw new RequestFailedException(500, e.getMessage());
            }
        }
    }
}
