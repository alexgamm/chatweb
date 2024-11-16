package chatweb.model.dto;

import chatweb.model.message.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String id;
    private int userId;
    private String username;
    private String message;
    private String room;
    private MessageDto repliedMessage;
    private Date sendDate;
    private List<Button> buttons;
}
