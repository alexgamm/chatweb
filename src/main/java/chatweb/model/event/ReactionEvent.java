package chatweb.model.event;

import chatweb.model.dto.MessageDto;

import java.util.Date;
import java.util.Set;

public class ReactionEvent extends Event {
    private final String messageId;
    private final Set<MessageDto.Reaction> reactions;

    public ReactionEvent(String messageId, Set<MessageDto.Reaction> reactions) {
        super(EventType.REACTION, new Date());
        this.messageId = messageId;
        this.reactions = reactions;
    }
}
