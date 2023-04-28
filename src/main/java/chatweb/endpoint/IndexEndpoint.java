package chatweb.endpoint;

import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import com.github.jknack.handlebars.Handlebars;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

public class IndexEndpoint extends AuthEndpoint {

    private final Handlebars handlebars;

    public IndexEndpoint(UserRepository userRepository, SessionRepository sessionRepository, Handlebars handlebars) {
        super(userRepository, sessionRepository);
        this.handlebars = handlebars;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        try {
            getUser(request);
        } catch (RequestFailedException e) {
            return Response.redirect("/login");
        }
        return new TemplateResponse.Builder(handlebars, "index").build();
    }
}
