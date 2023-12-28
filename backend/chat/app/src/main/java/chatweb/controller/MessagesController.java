package chatweb.controller;

import chatweb.client.EventsApiClient;
import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.MessageMapper;
import chatweb.model.api.*;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.DeletedMessageEvent;
import chatweb.model.event.EditedMessageEvent;
import chatweb.model.event.NewMessageEvent;
import chatweb.model.event.ServiceReactionEvent;
import chatweb.repository.MessageRepository;
import chatweb.repository.RoomRepository;
import chatweb.service.ChatGPTService;
import jakarta.persistence.criteria.Predicate;
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
    private final EventsApiClient eventsApi;
    private final ChatGPTService chatGPTService;
    private final RoomRepository roomRepository;

    @GetMapping
    public MessagesResponse getMessages(
            @RequestParam(required = false, defaultValue = "20") int count,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) String room,
            @RequestAttribute User user
    ) throws ApiErrorException {
        final Integer roomId;
        if (room != null) {
            roomId = roomRepository.findRoomIdByRoomKey(room);
            if (roomId == null) {
                throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "room not found"));
            }
        } else {
            roomId = null;
        }
        List<Message> messages = messageRepository.findAll(((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (roomId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("roomId"), roomId));
            }
            if (from != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("sendDate"), new Date(from)));
            }
            return predicate;
        }), Pageable.ofSize(count)).getContent();

        return new MessagesResponse(
                messages.stream()
                        .map(message -> MessageMapper.messageToMessageDto(message, user.getId(), true))
                        .toList()
        );
    }

    //@RequestBody SendMessageRequest body - позволяет спарсить тело запроса в нашу модель
    //TODO for v.dybysov remain what RespEntity is
    @PostMapping
    public MessageIdResponse sendMessage(
            @RequestBody SendMessageRequest body,
            @RequestAttribute User user
    ) throws ApiErrorException {
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "missing message"));
        }
        Room room = roomRepository.findByRoomKey(body.getRoom());
        if (!roomRepository.isUserInRoom(room == null ? null : room.getId(), user.getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.FORBIDDEN, "not allowed to send messages"));
        }
        Message repliedMessage = Optional.ofNullable(body.getRepliedMessageId())
                .flatMap(messageRepository::findById)
                .orElse(null);
        Message message = messageRepository.save(new Message(
                randomUUID().toString(),
                body.getMessage().trim(),
                room,
                user,
                Collections.emptySet(),
                repliedMessage,
                new Date()
        ));
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user.getId(), false);
        eventsApi.addEvent(new NewMessageEvent(messageDto, room == null ? null : room.getId()));
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
        eventsApi.addEvent(new DeletedMessageEvent(message.getId(), message.getRoomId()));
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
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user.getId(), true);
        eventsApi.addEvent(new EditedMessageEvent(
                MessageMapper.messageToMessageDto(message, null, false), message.getRoomId()
        ));
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
        if (!roomRepository.isUserInRoom(message.getRoomId(), user.getId())) {
            throw new ApiErrorException(new ApiError(
                    HttpStatus.FORBIDDEN,
                    "not allowed to send reactions in this room"
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
        eventsApi.addEvent(new ServiceReactionEvent(
                saved.getId(),
                saved.getReactions(),
                saved.getRoomId()
        ));
        return MessageMapper.messageToMessageDto(saved, user.getId(), true);
    }
}


