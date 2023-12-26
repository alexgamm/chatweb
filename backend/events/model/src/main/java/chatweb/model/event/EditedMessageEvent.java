package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;

import java.util.Date;

@Getter
public class EditedMessageEvent extends Event {
    private MessageDto message;

    public EditedMessageEvent() {
        super(EventType.EDITED_MESSAGE, null, null);
    }

    public EditedMessageEvent(MessageDto message, Integer roomId) {
        super(EventType.EDITED_MESSAGE, new Date(), roomId);
        this.message = message;
    }
}
