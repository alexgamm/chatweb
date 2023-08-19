package chatweb.service;

import chatweb.configuration.properties.ChatGPTProperties;
import chatweb.exception.chatgpt.ChatCompletionException;
import chatweb.model.chatgpt.ChatCompletionRequest;
import chatweb.model.chatgpt.ChatCompletionResponse;
import chatweb.model.chatgpt.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
@EnableConfigurationProperties(ChatGPTProperties.class)
public class ChatGPTService {
    private final static String CHAT_COMPLETION_URI = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper;
    private final ChatGPTProperties chatGPTProperties;
    private final HttpClient httpClient = HttpClients.createDefault();

    public ChatGPTService(ObjectMapper objectMapper, ChatGPTProperties chatGPTProperties) {
        this.objectMapper = objectMapper;
        this.chatGPTProperties = chatGPTProperties;
    }

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
}
