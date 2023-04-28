package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.longpoll.LongPollFuture;
import chatweb.model.Message;
import chatweb.model.MessagesResponse;
import chatweb.model.SendMessageRequest;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.MessageService;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Date;
import java.util.List;

public class MessagesEndpoint extends AuthEndpoint {
    private final MessageService messageService;

    public MessagesEndpoint(UserRepository userRepository, SessionRepository sessionRepository, MessageService messageService) {
        super(userRepository, sessionRepository);
        this.messageService = messageService;
    }

    @Override
    public Object authGet(Request request, User user) throws RequestFailedException {
        long ts;
        try {
            ts = Long.parseLong(request.getQuery().get("ts"));
        } catch (Throwable e) {
            throw new RequestFailedException(400, "incorrect ts");
        }
        List<Message> newMessages = messageService.getNewMessages(ts);
        LongPollFuture longPollFuture = new LongPollFuture(ts);
        if (newMessages.isEmpty()) {
            messageService.addLongPollFuture(longPollFuture);
        } else {
            longPollFuture.complete(newMessages);
        }
        userRepository.updateLastActivityAt(user.getId());
        return longPollFuture.thenApply(messages -> new MessagesResponse(messages));
    }

    @Override
    public Object authPost(Request request, User user) throws RequestFailedException {
        SendMessageRequest body = request.getBody(SendMessageRequest.class);

        if (body.getMessage() == null || body.getMessage().isBlank()) {
            return Response.badRequest("missing message");
        }
        messageService.addMessage(new Message(body.getMessage(), user.getUsername(), new Date()));
        return "";
    }
}
