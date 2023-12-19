package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;

@Getter
public class NewMessageEvent extends Event {
    private MessageDto message;

    public NewMessageEvent() {
        super(EventType.NEW_MESSAGE, null);
    }

    public NewMessageEvent(MessageDto message) {
        super(EventType.NEW_MESSAGE, message.getSendDate());
        this.message = message;
    }
}
