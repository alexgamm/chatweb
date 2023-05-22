package chatweb.model;

public class Message {
    private final String id;
    private final String message;
    private final String username;
    private final Message repliedMessage;

    public Message(String id, String message, String username, Message repliedMessage) {
        this.id = id;
        this.message = message;
        this.username = username;
        this.repliedMessage = repliedMessage;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public Message getRepliedMessage() {
        return repliedMessage;
    }
}
