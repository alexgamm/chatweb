package chatweb.listener;

import chatweb.client.ChatRpcClient;
import chatweb.client.OpenAiApiClient;
import chatweb.configuration.ChatGPTProperties;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static chatweb.utils.KafkaTopics.Events.CHATGPT_TO_PROCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTListener {
    private final OpenAiApiClient openAiApiClient;
    private final ChatRpcClient chatRpcClient;
    private final ChatGPTProperties chatGPTProperties;

    @KafkaListener(id = "chatgpt-processor", topics = CHATGPT_TO_PROCESS)
    public void handleNewMessageEvent(NewMessageEvent event) {
        MessageDto message = event.getMessage();
        String chatGPTMessageId = chatRpcClient.sendMessage(
                chatGPTProperties.getUserId(),
                chatGPTProperties.getLoadingMessage(),
                message.getId()
        );
        String completionContent;
        try {
            completionContent = openAiApiClient.getCompletionContent(
                    message.getMessage().replace(chatGPTProperties.getMention(), "")
            );
        } catch (Throwable e) {
            log.error("Could not get completion for message id {}", message.getId(), e);
            chatRpcClient.editMessage(chatGPTMessageId, chatGPTProperties.getErrorMessage());
            return;
        }
        chatRpcClient.editMessage(chatGPTMessageId, completionContent);
    }
}

