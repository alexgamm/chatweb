package chatweb.model.dto;

import chatweb.entity.Reaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Set;


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
    private Set<Reaction> reactions;

    @Getter
    public static class Reaction {
        private final String reaction;
        private final int count;

        public Reaction(String reaction, int count) {
            this.reaction = reaction;
            this.count = count;
        }
    }
}
