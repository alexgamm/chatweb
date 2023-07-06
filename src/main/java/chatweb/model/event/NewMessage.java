package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import chatweb.model.event.Event;
import chatweb.model.event.EventType;

public class NewMessage extends Event {
    private final MessageDto message;


    public NewMessage(MessageDto message) {
        super(EventType.NEW_MESSAGE, message.getSendDate());
        this.message = message;
    }

    public MessageDto getMessage() {
        return message;
    }
}
