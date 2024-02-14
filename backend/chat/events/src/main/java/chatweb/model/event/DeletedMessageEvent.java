package chatweb.model.event;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeletedMessageEvent extends MessageIdEvent {

    public DeletedMessageEvent(String room, String messageId) {
        super(room, messageId);
    }
}
