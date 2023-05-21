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

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

public class MessagesEndpoint extends AuthEndpoint {
    private final EventsService eventsService;
    private List<Message> allMessages = new ArrayList<>();


    public MessagesEndpoint(UserRepository userRepository, SessionRepository sessionRepository, EventsService eventsService) {
        super(userRepository, sessionRepository);
        this.eventsService = eventsService;
    }

    @Override
    public Object authPost(Request request, User user) throws RequestFailedException {
        SendMessageRequest body = request.getBody(SendMessageRequest.class);
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            return Response.badRequest("missing message");
        } else if (body.getRepliedMessageId() == null) {
            Message message = new Message(randomUUID().toString(), body.getMessage(), user.getUsername(), null);
            allMessages.add(message);
            eventsService.addEvent(new NewMessage(message));
        } else {
            String repliedMessageId = body.getRepliedMessageId();
            Message repliedMessage = allMessages.stream().filter(message -> message.getId().equals(repliedMessageId)).findAny().orElse(null);
            eventsService.addEvent(new NewMessage(
                    new Message(randomUUID().toString(), body.getMessage(), user.getUsername(),
                            repliedMessage))
            );
        }
        return "";
    }
}
