package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.model.Message;
import chatweb.model.NewMessage;
import chatweb.model.SendMessageRequest;
import chatweb.repository.MessageRepository;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;


import static java.util.UUID.randomUUID;

public class MessagesEndpoint extends AuthEndpoint {
    private final EventsService eventsService;
    private final MessageRepository messageRepository;


    public MessagesEndpoint(UserRepository userRepository, SessionRepository sessionRepository, EventsService eventsService, MessageRepository messageRepository) {
        super(userRepository, sessionRepository);
        this.eventsService = eventsService;
        this.messageRepository = messageRepository;
    }

    @Override
    public Object authPost(Request request, User user) throws RequestFailedException {
        SendMessageRequest body = request.getBody(SendMessageRequest.class);
        String repliedMessageId = body.getRepliedMessageId();
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            return Response.badRequest("missing message");
        } else if (body.getRepliedMessageId() == null) {
            Message message = new Message(randomUUID().toString(), body.getMessage(), user.getUsername(), null);
            messageRepository.saveMessage(message);
            eventsService.addEvent(new NewMessage(message));
        } else {
            Message repliedMessage = messageRepository.getRepliedMessage(repliedMessageId);
            Message message = new Message(randomUUID().toString(), body.getMessage(), user.getUsername(), repliedMessage);
            messageRepository.saveMessage(message);
            eventsService.addEvent(new NewMessage(message));

        }
        return "";
    }
}
