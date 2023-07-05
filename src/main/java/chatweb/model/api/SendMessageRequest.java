package chatweb.model.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessageRequest {
    private String message;
    private String id;
    private String repliedMessageId;
}
