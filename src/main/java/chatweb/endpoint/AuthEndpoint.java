package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;

import java.util.Optional;

public abstract class AuthEndpoint implements Endpoint {
    protected final UserRepository userRepository;
    protected final SessionRepository sessionRepository;

    public AuthEndpoint(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    protected User getUser(Request request) throws RequestFailedException {
        return Optional.ofNullable(request.getCookies().get("sessionId"))
                // с чем работаем -> что возвращаем
                .map(sessionId -> sessionRepository.findSessionById(sessionId))
                .map(session -> userRepository.findUserById(session.getUserId()))
                .orElseThrow(() -> new RequestFailedException(401, "unauthorized"));
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        return authGet(request, getUser(request));
    }

    @Override
    public Object post(Request request) throws RequestFailedException {
        return authPost(request, getUser(request));
    }

    public Object authGet(Request request, User user) throws RequestFailedException{
        throw new RequestFailedException(405, "method not allowed");
    }

    public Object authPost(Request request, User user) throws RequestFailedException{
        throw new RequestFailedException(405, "method not allowed");
    }

}
