package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ReactionEvent extends MessageIdEvent {
    private Set<MessageDto.Reaction> reactions;

    public ReactionEvent(String messageId, Set<MessageDto.Reaction> reactions) {
        super(messageId);
        this.reactions = reactions;
    }
}
