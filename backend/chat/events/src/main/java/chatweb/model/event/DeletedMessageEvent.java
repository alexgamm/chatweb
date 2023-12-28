package chatweb.model.event;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeletedMessageEvent extends MessageIdEvent {

    public DeletedMessageEvent(String messageId) {
        super(messageId);
    }
}
