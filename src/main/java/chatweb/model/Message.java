package chatweb.model;

import java.util.Date;

public class Message {
    private final String message;
    private final String username;
    private final Date sendDate;

    public Message(String message, String username, Date sendDate) {
        this.message = message;
        this.username = username;
        this.sendDate = sendDate;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public Date getSendDate() {
        return sendDate;
    }
}
