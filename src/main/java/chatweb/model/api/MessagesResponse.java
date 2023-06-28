package chatweb.model.api;

import chatweb.model.dto.MessageDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MessagesResponse {

    private final List<MessageDto> messages;
}
