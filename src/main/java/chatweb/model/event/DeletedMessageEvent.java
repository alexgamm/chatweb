package chatweb.model.event;

import lombok.Getter;

import java.util.Date;

@Getter
public class DeletedMessageEvent extends Event {
    private final String messageId;

    public DeletedMessageEvent(String messageId) {
        super(EventType.DELETED_MESSAGE, new Date());
        this.messageId = messageId;
    }
}
