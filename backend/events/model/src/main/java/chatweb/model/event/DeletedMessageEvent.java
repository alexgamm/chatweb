package chatweb.model.event;

import lombok.Getter;

import java.util.Date;

@Getter
public class DeletedMessageEvent extends Event {
    private String messageId;

    public DeletedMessageEvent() {
        super(EventType.DELETED_MESSAGE, null);
    }

    public DeletedMessageEvent(String messageId) {
        super(EventType.DELETED_MESSAGE, new Date());
        this.messageId = messageId;
    }
}
