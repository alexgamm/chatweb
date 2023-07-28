package chatweb.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class MessageDto {

    private final String id;
    private final int userId;
    private final String username;
    private final String message;
    private final MessageDto repliedMessage;
    private final Date sendDate;
}
