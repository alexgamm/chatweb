package chatweb.model.event;

import chatweb.entity.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ServiceReactionEvent extends MessageIdEvent {
    private Set<Reaction> reactions;

    public ServiceReactionEvent(String messageId, Set<Reaction> reactions) {
        super(messageId);
        this.reactions = reactions;
    }
}
