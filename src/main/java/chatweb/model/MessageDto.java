package chatweb.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class MessageDto {

    private final String id;
    private final String message;
    private final String username;
    private final MessageDto repliedMessage;
    private final Date sendDate;
}
