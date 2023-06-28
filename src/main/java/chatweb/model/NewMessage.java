package chatweb.model;

import java.util.Date;

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
