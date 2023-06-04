package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.model.*;
import chatweb.repository.MessageRepository;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.UUID.randomUUID;

public class MessagesEndpoint extends AuthEndpoint {
    private final EventsService eventsService;
    private final MessageRepository messageRepository;
    private final List<Message> messages = new ArrayList<>();


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
        if (Long.parseLong(request.getQuery().get("from"))  == 0) {
            List<MessagesResponse.Message> initialMessages = messageRepository.getSeveralMessages(count).stream()
                    .map(message -> new MessagesResponse.Message(
                            message.getUsername(),
                            message.getMessage(),
                            message.getId(),
                            message.getSendDate(),
                            message.getRepliedMessage() == null ? null : message.getRepliedMessage())
                    )
                    .toList();
            return new MessagesResponse(initialMessages);
        } else {
            try {
                from = new Date(Long.parseLong(request.getQuery().get("from")));
            } catch (Throwable e) {
                throw new RequestFailedException(400, "incorrect date");
            }
            List<MessagesResponse.Message> nextMessages = messageRepository.getSeveralMessagesFrom(count, from).stream()
                    .map(message -> new MessagesResponse.Message(
                            message.getUsername(),
                            message.getMessage(),
                            message.getId(),
                            message.getSendDate(),
                            message.getRepliedMessage() == null ? null : message.getRepliedMessage())
                    )
                    .toList();
            return new MessagesResponse(nextMessages);
        }
    }

    @Override
    public Object authPost(Request request, User user) throws RequestFailedException {
        SendMessageRequest body = request.getBody(SendMessageRequest.class);
        String repliedMessageId = body.getRepliedMessageId();
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            return Response.badRequest("missing message");
        } else {
            Message repliedMessage = messageRepository.getMessage(repliedMessageId);
            Message message = new Message(
                    randomUUID().toString(),
                    body.getMessage(),
                    user.getUsername(),
                    body.getRepliedMessageId() == null ? null : repliedMessage,
                    new Timestamp(new Date().getTime())
            );
            messageRepository.saveMessage(message);
            messages.add(message);
            eventsService.addEvent(new NewMessage(message));
        }
        return "";
    }

}
