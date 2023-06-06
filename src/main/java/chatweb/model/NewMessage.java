package chatweb.model;

public class NewMessage extends Event{
    private final Message message;

    public NewMessage(Message message) {
        super(EventType.NEW_MESSAGE, message.getSendDate());
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
