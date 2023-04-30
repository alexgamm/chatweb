package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.model.Message;
import chatweb.model.NewMessage;
import chatweb.model.SendMessageRequest;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Date;

public class MessagesEndpoint extends AuthEndpoint {
    private final EventsService eventsService;

    public MessagesEndpoint(UserRepository userRepository, SessionRepository sessionRepository, EventsService eventsService) {
        super(userRepository, sessionRepository);
        this.eventsService = eventsService;
    }

    @Override
    public Object authPost(Request request, User user) throws RequestFailedException {
        SendMessageRequest body = request.getBody(SendMessageRequest.class);

        if (body.getMessage() == null || body.getMessage().isBlank()) {
            return Response.badRequest("missing message");
        }
        eventsService.addEvent(new NewMessage(new Message(body.getMessage(), user.getUsername())));
        return "";
    }
}
