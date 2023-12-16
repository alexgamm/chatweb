package chatweb.model.chatgpt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RequiredArgsConstructor
@Getter
public class ChatCompletionRequest {
    private final String model;
    private final List<Message> messages;
}
