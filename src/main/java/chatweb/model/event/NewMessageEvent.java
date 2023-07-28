package chatweb.model.event;

import chatweb.model.dto.MessageDto;

public class NewMessageEvent extends Event {
    private final MessageDto message;


    public NewMessageEvent(MessageDto message) {
        super(EventType.NEW_MESSAGE, message.getSendDate());
        this.message = message;
    }

    public MessageDto getMessage() {
        return message;
    }
}
