package chatweb.model;

import java.util.ArrayList;
import java.util.List;

public class MessagesResponse {
    private final List<Message> messages;

    public MessagesResponse(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
