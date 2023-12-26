package chatweb.model.event;

import lombok.Getter;

import java.util.Date;

@Getter
public class DeletedMessageEvent extends Event {
    private String messageId;

    public DeletedMessageEvent() {
        super(EventType.DELETED_MESSAGE, null, null);
    }

    public DeletedMessageEvent(String messageId, Integer roomId) {
        super(EventType.DELETED_MESSAGE, new Date(), roomId);
        this.messageId = messageId;
    }
}
