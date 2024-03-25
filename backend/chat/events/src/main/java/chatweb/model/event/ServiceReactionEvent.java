package chatweb.model.event;

import chatweb.entity.Reaction;
import chatweb.mapper.MessageMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ServiceReactionEvent extends MessageIdEvent implements PersonalEventProducer {
    private Set<Reaction> reactions;

    public ServiceReactionEvent(String room, String messageId, Set<Reaction> reactions) {
        super(room, messageId);
        this.reactions = reactions;
    }

    @Override
    public IEvent getPersonalEvent(Integer userId) {
        return new ReactionEvent(
                getRoom(),
                getMessageId(),
                MessageMapper.groupReactions(getReactions(), userId)
        );
    }
}
