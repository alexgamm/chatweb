package chatweb.model;

public class SendMessageRequest {
    private String message;
    private String repliedMessageId;

    public SendMessageRequest() {
    }

    public String getMessage() {
        return message;
    }

    public String getRepliedMessageId() {
        return repliedMessageId;
    }
}
