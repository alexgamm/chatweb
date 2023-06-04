package chatweb.model;

import java.util.Date;

public class Message {
    private final String id;
    private final String message;
    private final String username;
    private final Message repliedMessage;
    private final Date sendDate;

    public Message(String id, String message, String username, Message repliedMessage, Date sendDate) {
        this.id = id;
        this.message = message;
        this.username = username;
        this.repliedMessage = repliedMessage;
        this.sendDate = sendDate;
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

    public Date getSendDate() {
        return sendDate;
    }
}
