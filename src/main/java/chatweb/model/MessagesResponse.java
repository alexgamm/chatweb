package chatweb.model;

import java.util.Date;
import java.util.List;

public class MessagesResponse {
    public static class Message {
        private final String username;
        private final String message;
        private final String id;
        private final Date sendDate;
        private final chatweb.entity.Message repliedMessage;

        public Message(String username, String message, String id, Date sendDate, chatweb.entity.Message repliedMessage) {
            this.username = username;
            this.message = message;
            this.id = id;
            this.sendDate = sendDate;
            this.repliedMessage = repliedMessage;
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }

        public String getId() {
            return id;
        }

        public Date getSendDate() {
            return sendDate;
        }

        public chatweb.entity.Message getRepliedMessage() {
            return repliedMessage;
        }
    }
    private final List<Message> messages;

    public MessagesResponse(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

}
