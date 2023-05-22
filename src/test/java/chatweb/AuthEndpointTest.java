package chatweb;

import chatweb.endpoint.AuthEndpoint;
import chatweb.entity.Session;
import chatweb.entity.User;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import webserver.Request;
import webserver.RequestFailedException;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthEndpointTest {
    private AuthEndpoint authEndpoint;

    @Test
    public void testUnauthorized() {
        Request request = mock(Request.class);
        when(request.getCookies()).thenReturn(Collections.emptyMap());
        UserRepository userRepository = mock(UserRepository.class);
        SessionRepository sessionRepository = mock(SessionRepository.class);
        authEndpoint = new AuthEndpoint(userRepository, sessionRepository) {
            @Override
            public Object authGet(Request request, User user) throws RequestFailedException {
                return "ok";
            }
        };
        try {
            authEndpoint.get(request);
            Assertions.fail("should throw RequestFailedException");
        } catch (RequestFailedException ex) {
            Assertions.assertEquals(401, ex.getStatusCode());
        }
    }

    @Test
    public void testAuthorized() {
        User user = new User(45, "sasha", "zzaaqq", new Date());
        Session session = new Session("4545", 45);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findUserById(45)).thenReturn(user);
        SessionRepository sessionRepository = mock(SessionRepository.class);
        when(sessionRepository.findSessionById("4545")).thenReturn(session);
        Request request = mock(Request.class);
        when(request.getCookies()).thenReturn(Collections.singletonMap("sessionId", "4545"));
        authEndpoint = new AuthEndpoint(userRepository, sessionRepository) {
            @Override
            public Object authGet(Request request, User user) throws RequestFailedException {
                return user.getUsername();
            }
        };
        try {
            Assertions.assertEquals("sasha", authEndpoint.get(request));
        } catch (RequestFailedException ex) {
            Assertions.fail("should not throw RequestFailedException");
        }
    }
}
