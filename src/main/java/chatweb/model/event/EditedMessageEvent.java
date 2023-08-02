package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;

import java.util.Date;

@Getter
public class EditedMessageEvent extends Event {
    private final MessageDto message;

    public EditedMessageEvent(MessageDto message) {
        super(EventType.EDITED_MESSAGE, new Date());
        this.message = message;
    }
}
