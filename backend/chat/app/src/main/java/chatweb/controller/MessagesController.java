package chatweb.controller;

import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.exception.InvalidRoomKeyException;
import chatweb.mapper.MessageMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.EmptyResponse;
import chatweb.model.api.MessageIdResponse;
import chatweb.model.api.MessagesResponse;
import chatweb.model.api.ReactionRequest;
import chatweb.model.api.SendMessageRequest;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.DeletedMessageEvent;
import chatweb.model.event.EditedMessageEvent;
import chatweb.model.event.NewMessageEvent;
import chatweb.model.event.ServiceReactionEvent;
import chatweb.model.event.TypingEvent;
import chatweb.producer.EventsKafkaProducer;
import chatweb.repository.MessageRepository;
import chatweb.repository.RoomRepository;
import chatweb.service.ChatGPTService;
import chatweb.utils.RoomUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final ChatGPTService chatGPTService;
    private final RoomRepository roomRepository;
    private final EventsKafkaProducer eventsProducer;


    @GetMapping
    public MessagesResponse getMessages(
            @RequestParam(required = false, defaultValue = "20") int count,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) String room,
            @RequestAttribute User user
    ) throws ApiErrorException {
        final Integer roomId;
        if (room != null) {
            roomId = roomRepository.findRoomIdByKey(room);
            if (roomId == null) {
                throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "room not found"));
            }
        } else {
            roomId = null;
        }
        // TODO Check if user is member
        List<Message> messages = messageRepository.findMessages(count, roomId, from);
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
        Room room;
        if (body.getRoom() != null) {
            room = roomRepository.findByKey(body.getRoom());
            if (room == null) {
                throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "room doesn't exist"));
            }
        } else {
            room = null;
        }
        if (room != null && !roomRepository.isUserInRoom(room.getId(), user.getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.FORBIDDEN, "you are not in the room"));
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
                new Date(),
                Collections.emptyList()
        ));
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user.getId(), false);
        eventsProducer.addEvent(new NewMessageEvent(messageDto));
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
        eventsProducer.addEvent(new DeletedMessageEvent(message.getRoomKey(), message.getId()));
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
        eventsProducer.addEvent(new EditedMessageEvent(
                MessageMapper.messageToMessageDto(message, null, false)
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
        if (message.getRoomId() != null && !roomRepository.isUserInRoom(message.getRoomId(), user.getId())) {
            throw new ApiErrorException(new ApiError(
                    HttpStatus.FORBIDDEN,
                    "you are not in the room"
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
        eventsProducer.addEvent(new ServiceReactionEvent(
                saved.getRoomKey(),
                saved.getId(),
                saved.getReactions()
        ));
        return MessageMapper.messageToMessageDto(saved, user.getId(), true);
    }

    @PutMapping("typing")
    public ResponseEntity<EmptyResponse> typing(
            @RequestAttribute User user,
            @RequestParam(required = false) String room
    ) throws ApiErrorException {
        Integer roomId;
        if (room != null) {
            try {
                roomId = RoomUtils.idFromKey(room);
            } catch (InvalidRoomKeyException e) {
                throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "Invalid key format"));
            }
        } else {
            roomId = null;
        }
        if (roomId != null && !roomRepository.isUserInRoom(roomId, user.getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "you are not in the room"));
        }
        eventsProducer.addEvent(new TypingEvent(room, user.getId()));
        return ResponseEntity.ok(new EmptyResponse());
    }
}


