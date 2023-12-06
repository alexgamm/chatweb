package chatweb.controller;

import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.MessageMapper;
import chatweb.model.api.*;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.DeletedMessageEvent;
import chatweb.model.event.EditedMessageEvent;
import chatweb.model.event.NewMessageEvent;
import chatweb.model.event.ReactionEvent;
import chatweb.repository.MessageRepository;
import chatweb.service.ChatGPTService;
import chatweb.service.EventsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessagesController implements ApiControllerHelper {

    private final MessageRepository messageRepository;
    private final EventsService eventsService;
    private final ChatGPTService chatGPTService;

    @GetMapping
    public MessagesResponse getMessages(
            @RequestParam(required = false, defaultValue = "20") int count,
            @RequestParam(required = false) Long from,
            @RequestAttribute User user
    ) {
        // TODO ?
        List<Message> messages = from == null
                ? messageRepository.findByOrderBySendDateDesc(Pageable.ofSize(count))
                : messageRepository.findBySendDateBeforeOrderBySendDateDesc(new Date(from), Pageable.ofSize(count));
        return new MessagesResponse(
                messages.stream()
                        .map(message -> MessageMapper.messageToMessageDto(message, user, true))
                        .toList()
        );
    }

    //@RequestBody SendMessageRequest body - позволяет спарсить тело зепроса в нашу модель
    //TODO for v.dybysov remain what RespEntity is
    @PostMapping
    public MessageIdResponse sendMessage(
            @RequestBody SendMessageRequest body,
            @RequestAttribute User user
    ) throws ApiErrorException {
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            // ResponseEntity<String> = в парамтипе тело ответа, later =  Response.badRequest("missing message")
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "missing message"));
        }
        Message repliedMessage = Optional.ofNullable(body.getRepliedMessageId())
                .flatMap(repliedMessageId -> messageRepository.findById(repliedMessageId))
                .orElse(null);
        Message message = messageRepository.save(new Message(
                randomUUID().toString(),
                body.getMessage().trim(),
                user,
                Collections.emptySet(),
                repliedMessage,
                new Date()
        ));
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user, false);
        eventsService.addEvent(new NewMessageEvent(messageDto));
        chatGPTService.handleMessage(message);
        return new MessageIdResponse(message.getId());
    }

    @DeleteMapping("{messageId}")
    @Transactional
    public MessageIdResponse deleteMessage(
            @PathVariable String messageId,
            @RequestAttribute User user
    ) throws ApiErrorException {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "no message for delete"));
        }
        if (!user.getId().equals(message.getUser().getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.FORBIDDEN, "you can delete only your messages"));
        }
        messageRepository.deleteMessageById(message.getId());
        eventsService.addEvent(new DeletedMessageEvent(message.getId()));
        return new MessageIdResponse(messageId);
    }

    @PatchMapping("{messageId}")
    public MessageDto editMessage(
            @PathVariable String messageId,
            @RequestBody MessageDto body,
            @RequestAttribute User user
    ) throws ApiErrorException {
        if (body.getMessage() == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "message text is required"));
        }
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "no message for edit"));
        }
        if (!user.getId().equals(message.getUser().getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.FORBIDDEN, "you can edit only your messages"));
        }
        message.setMessage(body.getMessage().trim());
        messageRepository.save(message);
        // TODO check personal event
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user, true);
        eventsService.addEvent(new EditedMessageEvent(messageDto));
        return messageDto;
    }

    @PutMapping("{messageId}/reactions")
    public MessageDto toggleReaction(
            @PathVariable String messageId,
            @RequestBody ReactionRequest body,
            @RequestAttribute User user
    ) throws ApiErrorException {
        if (body.getReaction() == null || body.getReaction().isBlank()) {
            throw new ApiErrorException(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "reaction is required"
            ));
        }
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new ApiErrorException(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "cannot put reaction without message"
            ));
        }
        Reaction existingReaction = message.getReactions().stream()
                .filter(r -> r.getReaction().equals(body.getReaction())
                        && r.getUserId() == user.getId())
                .findFirst().orElse(null);
        if (existingReaction == null) {
            message.getReactions().add(new Reaction(
                    null,
                    message,
                    user.getId(),
                    body.getReaction()
            ));
        } else {
            message.getReactions().remove(existingReaction);
        }
        Message saved = messageRepository.save(message);
        eventsService.addEvent((userId) -> new ReactionEvent(
                saved.getId(),
                MessageMapper.groupReactions(saved.getReactions(), userId)
        ));
        return MessageMapper.messageToMessageDto(saved, user, true);
    }
}


