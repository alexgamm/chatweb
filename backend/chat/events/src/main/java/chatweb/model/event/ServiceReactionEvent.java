package chatweb.model.event;

import chatweb.entity.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ServiceReactionEvent extends MessageIdEvent {
    private Set<Reaction> reactions;

    public ServiceReactionEvent(Integer roomId, String messageId, Set<Reaction> reactions) {
        super(roomId, messageId);
        this.reactions = reactions;
    }
}
