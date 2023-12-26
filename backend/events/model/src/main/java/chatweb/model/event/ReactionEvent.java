package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;

import java.util.Date;
import java.util.Set;

@Getter
public class ReactionEvent extends Event {
    private String messageId;
    private Set<MessageDto.Reaction> reactions;

    public ReactionEvent() {
        super(EventType.REACTION, null, null);
    }

    public ReactionEvent(String messageId, Set<MessageDto.Reaction> reactions, Integer roomId) {
        super(EventType.REACTION, new Date(), roomId);
        this.messageId = messageId;
        this.reactions = reactions;
    }
}
