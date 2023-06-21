package chatweb.controller;

import chatweb.entity.Message;
import chatweb.entity.User;
import chatweb.model.MessagesResponse;
import chatweb.model.NewMessage;
import chatweb.model.SendMessageRequest;
import chatweb.repository.MessageRepository;
import chatweb.service.EventsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessagesController {

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
                        .map(message -> new MessagesResponse.Message(
                                message.getUsername(),
                                message.getMessage(),
                                message.getId(),
                                message.getSendDate(),
                                message.getRepliedMessage() // TODO Use dto to prevent infinite relations
                        ))
                        .toList()
        );
    }

    //@RequestBody SendMessageRequest body - позволяет спарсить тело зепроса в нашу модель
    //TODO for v.dybysov remain what RespEntity is
    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequest body, HttpServletRequest request) {
        if (body.getMessage() == null || body.getMessage().isBlank()) {
            // ResponseEntity<String> = в парамтипе тело ответа, later =  Response.badRequest("missing message")
            return ResponseEntity.badRequest().body("missing message");
        }
        Message repliedMessage = Optional.ofNullable(body.getRepliedMessageId())
                .flatMap(repliedMessageId -> messageRepository.findById(repliedMessageId))
                .orElse(null);
        User user = (User) request.getAttribute("user");
        Message message = new Message(
                randomUUID().toString(),
                body.getMessage(),
                user.getUsername(),
                repliedMessage,
                new Date()
        );
        messageRepository.save(message);
        eventsService.addEvent(new NewMessage(message));
        return ResponseEntity.ok("");
    }
}


