package chatweb.controller;

import chatweb.entity.Message;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.MessageMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.MessageIdResponse;
import chatweb.model.api.MessagesResponse;
import chatweb.model.api.SendMessageRequest;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.DeletedMessage;
import chatweb.model.event.NewMessage;
import chatweb.repository.MessageRepository;
import chatweb.service.EventsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public MessagesResponse getMessages(
            @RequestParam(required = false, defaultValue = "20") int count,
            @RequestParam(required = false) Long from
    ) {
        List<Message> messages = from == null
                ? messageRepository.findByOrderBySendDateDesc(Pageable.ofSize(count))
                : messageRepository.findBySendDateBeforeOrderBySendDateDesc(new Date(from), Pageable.ofSize(count));
        return new MessagesResponse(
                messages.stream()
                        .map(message -> MessageMapper.messageToMessageDto(message)) // TODO Use dto to prevent infinite relations
                        .toList()
        );
    }

    //@RequestBody SendMessageRequest body - позволяет спарсить тело зепроса в нашу модель
    //TODO for v.dybysov remain what RespEntity is
    @PostMapping
    public MessageIdResponse sendMessage(@RequestBody SendMessageRequest body, HttpServletRequest request) throws ApiErrorException {
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            // ResponseEntity<String> = в парамтипе тело ответа, later =  Response.badRequest("missing message")
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "missing message"));
        }
        Message repliedMessage = Optional.ofNullable(body.getRepliedMessageId())
                .flatMap(repliedMessageId -> messageRepository.findById(repliedMessageId))
                .orElse(null);
        User user = (User) request.getAttribute("user");
        Message message = messageRepository.save(new Message(
                randomUUID().toString(),
                body.getMessage(),
                user,
                repliedMessage,
                new Date()
        ));
        MessageDto messageDto = MessageMapper.messageToMessageDto(message);
        eventsService.addEvent(new NewMessage(messageDto));
        return new MessageIdResponse(message.getId());
    }

    @DeleteMapping("{messageId}")
    @Transactional
    public MessageIdResponse deleteMessage(@PathVariable String messageId, @RequestAttribute User user) throws ApiErrorException {
        Message message = messageRepository.findMessageById(messageId);
        if (message == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "no message for delete"));
        }
        if (!user.getId().equals(message.getUser().getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.FORBIDDEN, "you can delete only your messages"));
        }
        messageRepository.deleteMessageById(message.getId());
        eventsService.addEvent(new DeletedMessage(message.getId()));
        return new MessageIdResponse(messageId);
    }
}


