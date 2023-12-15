package chatweb.service;

import chatweb.configuration.properties.ChatGPTProperties;
import chatweb.entity.User;
import chatweb.exception.chatgpt.ChatCompletionException;
import chatweb.mapper.MessageMapper;
import chatweb.model.chatgpt.ChatCompletionRequest;
import chatweb.model.chatgpt.ChatCompletionResponse;
import chatweb.model.chatgpt.Message;
import chatweb.model.event.NewMessageEvent;
import chatweb.repository.MessageRepository;
import chatweb.repository.UserRepository;
import chatweb.utils.UserColorUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableConfigurationProperties(ChatGPTProperties.class)
@RequiredArgsConstructor
public class ChatGPTService {
    private final static String CHAT_COMPLETION_URI = "https://api.openai.com/v1/chat/completions";
    private final TaskScheduler taskScheduler;
    private final ChatGPTProperties chatGPTProperties;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EventsService eventsService;
    private final WebClient webClient;


    public String getCompletionContent(String message) throws ChatCompletionException {
        ChatCompletionRequest completionRequest = new ChatCompletionRequest(
                chatGPTProperties.getModel(),
                Collections.singletonList(new Message("user", message))
        );
        ChatCompletionResponse chatCompletionResponse = webClient.post()
                .uri(CHAT_COMPLETION_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(completionRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatGPTProperties.getToken())
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ChatCompletionException(String
                                        .format("Unsuccessful response: Status code: %d, Body: %s",
                                                clientResponse.statusCode().value(), errorBody
                                        ))));
                    } else {
                        return clientResponse.bodyToMono(ChatCompletionResponse.class);
                    }
                })
                .block();

        return Optional.ofNullable(chatCompletionResponse)
                .map(ChatCompletionResponse::getChoices)
                .flatMap(list -> list.stream().findFirst())
                .map(ChatCompletionResponse.Choice::getMessage)
                .map(Message::getContent)
                .orElseThrow(() -> new ChatCompletionException("Empty choices were received or response was null"));
    }

    public void handleMessage(chatweb.entity.Message message) {
        String mention = "@" + chatGPTProperties.getUsername();
        User user = getOrCreateUser();
        boolean isChatGPTMentioned = message.getMessage().startsWith(mention);
        boolean isReplyToChatGPT = Optional.ofNullable(message.getRepliedMessage())
                .map(chatweb.entity.Message::getUser)
                .map(User::getId)
                .map(id -> id.equals(user.getId()))
                .orElse(false);
        if (!isChatGPTMentioned && !isReplyToChatGPT) {
            return;
        }
        taskScheduler.schedule(() -> {
            try {
                String messageToComplete = isChatGPTMentioned
                        ? message.getMessage().replace(mention, "")
                        : message.getMessage();
                String completionContent = getCompletionContent(messageToComplete);
                chatweb.entity.Message completionMessage = messageRepository.save(new chatweb.entity.Message(
                        UUID.randomUUID().toString(),
                        completionContent,
                        user,
                        null,
                        message,
                        new Date()
                ));
                eventsService.addEvent(new NewMessageEvent(MessageMapper.messageToMessageDto(completionMessage, null, false)));
            } catch (ChatCompletionException e) {
                // TODO handle properly
                throw new RuntimeException(e);
            }
        }, Instant.now());
    }

    public User getOrCreateUser() {
        return Optional.ofNullable(userRepository.findUserByUsername(chatGPTProperties.getUsername()))
                .orElseGet(() -> userRepository.save(new User(
                        null,
                        chatGPTProperties.getUsername(),
                        null,
                        null,
                        null,
                        UserColorUtils.getRandomColor()
                )));
    }
}
