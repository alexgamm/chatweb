package chatweb.model.event;

import chatweb.entity.Reaction;
import lombok.Getter;

import java.util.Date;
import java.util.Set;

@Getter
public class ServiceReactionEvent extends Event {
    private String messageId;
    private Set<Reaction> reactions;

    public ServiceReactionEvent() {
        super(EventType.SERVICE_REACTION, null);
    }

    public ServiceReactionEvent(String messageId, Set<Reaction> reactions) {
        super(EventType.SERVICE_REACTION, new Date());
        this.messageId = messageId;
        this.reactions = reactions;
    }
}