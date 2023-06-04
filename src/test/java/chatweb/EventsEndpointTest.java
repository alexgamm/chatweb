package chatweb;

import chatweb.endpoint.EventsEndpoint;
import chatweb.entity.User;
import chatweb.model.EventsResponse;
import chatweb.model.Message;
import chatweb.model.NewMessage;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import webserver.Request;
import webserver.RequestFailedException;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventsEndpointTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testGetAndAddEvent() throws RequestFailedException, ExecutionException, InterruptedException, TimeoutException {
        UserRepository userRepository = mock(UserRepository.class);
        SessionRepository sessionRepository = mock(SessionRepository.class);
        Request request = mock(Request.class);
        when(request.getQuery()).thenReturn(Collections.singletonMap("ts", "0"));
        User user = new User(4656, "name", "afll", new Date());
        EventsService eventsService = new EventsService();
        EventsEndpoint eventsEndpoint = new EventsEndpoint(userRepository, sessionRepository, eventsService);

        CompletableFuture<EventsResponse> future = (CompletableFuture<EventsResponse>) eventsEndpoint.authGet(request, user);
        EventsResponse eventsResponse = future.get(10, TimeUnit.SECONDS);

        when(request.getQuery()).thenReturn(Collections.singletonMap("ts", String.valueOf(eventsResponse.getEvents().get(0).getDate().getTime())));
        future = (CompletableFuture<EventsResponse>) eventsEndpoint.authGet(request, user);
        Assertions.assertFalse(future.isDone());

        NewMessage newMessage = new NewMessage(new Message("1", "hi", user.getUsername(), null, new Date(Long.parseLong("1685282664601"))));
        eventsService.addEvent(newMessage);
        eventsResponse = future.get(10, TimeUnit.SECONDS);
        Assertions.assertEquals(newMessage, eventsResponse.getEvents().get(0));
    }
}
