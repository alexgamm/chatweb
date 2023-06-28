package chatweb.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MessagesResponse {

    private final List<MessageDto> messages;
}
