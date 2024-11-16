package chatweb.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReactionEvent extends MessageIdEvent {

    public ReactionEvent(String room, String messageId) {
        super(room, messageId);
    }
}
