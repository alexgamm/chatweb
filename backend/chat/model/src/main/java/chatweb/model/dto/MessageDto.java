package chatweb.model.dto;

import chatweb.model.message.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Getter
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
    private Set<Reaction> reactions;
    private List<Button> buttons;

    @Getter
    @RequiredArgsConstructor
    public static class Reaction {
        private final String reaction;
        private final int count;
        private final boolean hasOwn;

    }
}
