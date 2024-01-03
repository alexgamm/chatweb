package chatweb.model.event;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeletedMessageEvent extends MessageIdEvent {

    public DeletedMessageEvent(Integer roomId, String messageId) {
        super(roomId, messageId);
    }
}
