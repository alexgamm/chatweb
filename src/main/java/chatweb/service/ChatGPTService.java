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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final ObjectMapper objectMapper;
    private final ChatGPTProperties chatGPTProperties;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EventsService eventsService;
    private final HttpClient httpClient = HttpClients.createDefault();

    public String getCompletionContent(String message) throws IOException, ChatCompletionException {
        HttpPost request = new HttpPost(CHAT_COMPLETION_URI);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + chatGPTProperties.getToken());
        ChatCompletionRequest completionRequest = new ChatCompletionRequest(
                chatGPTProperties.getModel(),
                Collections.singletonList(new Message("user", message))
        );
        request.setEntity(new StringEntity(
                objectMapper.writeValueAsString(completionRequest),
                ContentType.APPLICATION_JSON
        ));
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new ChatCompletionException("Unsuccessful response: " + responseBody);
        }
        ChatCompletionResponse chatCompletionResponse = objectMapper.readValue(responseBody, ChatCompletionResponse.class);
        return chatCompletionResponse.getChoices().stream()
                .findFirst()
                .map(ChatCompletionResponse.Choice::getMessage)
                .map(Message::getContent)
                .orElseThrow(() -> new ChatCompletionException("Empty choices were received"));
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
            } catch (IOException | ChatCompletionException e) {
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
