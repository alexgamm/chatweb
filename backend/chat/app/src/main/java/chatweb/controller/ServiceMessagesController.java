package chatweb.controller;

import chatweb.client.EventsApiClient;
import chatweb.entity.Message;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.MessageMapper;
import chatweb.model.api.MessageIdResponse;
import chatweb.model.api.service.SendServiceMessageRequest;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.NewMessageEvent;
import chatweb.repository.MessageRepository;
import chatweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;

import static chatweb.model.api.ApiError.badRequest;
import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api/service/messages")
@RequiredArgsConstructor
public class ServiceMessagesController {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EventsApiClient eventsApi;

    @PostMapping
    public MessageIdResponse sendMessage(
            @RequestBody SendServiceMessageRequest body
    ) throws ApiErrorException {
        User user = userRepository.findUserById(body.getUserId());
        if (user == null) {
            throw badRequest("Message sender not found").toException();
        }
        Message message = messageRepository.save(new Message(
                randomUUID().toString(),
                body.getMessage().trim(),
                null,
                user,
                Collections.emptySet(),
                null,
                new Date(),
                body.getButtons()
        ));
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user.getId(), false);
        eventsApi.addEvent(new NewMessageEvent(messageDto));
        return new MessageIdResponse(message.getId());
    }
}
