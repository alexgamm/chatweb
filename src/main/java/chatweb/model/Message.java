package chatweb.model;

public class Message {
    private final String message;
    private final String username;

    public Message(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

}
