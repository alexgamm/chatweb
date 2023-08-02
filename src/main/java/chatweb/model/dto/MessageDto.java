package chatweb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String id;
    private int userId;
    private String username;
    private String message;
    private MessageDto repliedMessage;
    private Date sendDate;
}
