package chatweb.service;

import chatweb.client.ChatRpcClient;
import chatweb.client.OpenAiApiClient;
import chatweb.exception.chatgpt.ChatCompletionException;
import chatweb.model.event.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static chatweb.utils.KafkaTopics.OPENAI;

@Service
@RequiredArgsConstructor
public class ChatGPTService {
    private final OpenAiApiClient openAiApiClient;
    private final ChatRpcClient chatRpcClient;

    @KafkaListener(topics = OPENAI)
    public void handleMessage(NewMessageEvent message) {
        try {
            // TODO trim GOSHA for api message
            String completionContent = openAiApiClient.getCompletionContent(message.getMessage().getMessage());
            chatRpcClient.sendMessage(-1, completionContent, Collections.emptyList());
        } catch (ChatCompletionException e) {
            throw new RuntimeException(e);
        }
    }
}

