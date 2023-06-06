package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.model.Message;
import chatweb.model.MessagesResponse;
import chatweb.model.NewMessage;
import chatweb.model.SendMessageRequest;
import chatweb.repository.MessageRepository;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Date;
import java.util.List;

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
    public Object authGet(Request request, User user) throws RequestFailedException {
        int count;
        Date from;
        try {
            count = Integer.parseInt(request.getQuery().get("count"));
        } catch (Throwable e) {
            throw new RequestFailedException(400, "incorrect count value");
        }
        try {
            from = new Date(Long.parseLong(request.getQuery().get("from")));
        } catch (Throwable e) {
            from = null;
        }
        List<MessagesResponse.Message> initialMessages = messageRepository.getSeveralMessagesFrom(count, from).stream()
                .map(message -> new MessagesResponse.Message(
                        message.getUsername(),
                        message.getMessage(),
                        message.getId(),
                        message.getSendDate(),
                        message.getRepliedMessage() == null ? null : message.getRepliedMessage())
                )
                .toList();
        return new MessagesResponse(initialMessages);
    }

    @Override
    public Object authPost(Request request, User user) throws RequestFailedException {
        SendMessageRequest body = request.getBody(SendMessageRequest.class);
        String repliedMessageId = body.getRepliedMessageId();
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            return Response.badRequest("missing message");
        }
        Message repliedMessage = messageRepository.getMessage(repliedMessageId, false);
        Message message = new Message(
                randomUUID().toString(),
                body.getMessage(),
                user.getUsername(),
                repliedMessage,
                new Date()
        );
        messageRepository.saveMessage(message);
        eventsService.addEvent(new NewMessage(message));
        return "";
    }

}
