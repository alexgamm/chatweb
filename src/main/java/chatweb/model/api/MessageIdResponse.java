package chatweb.model.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MessageIdResponse {
    private final String messageId;
}
