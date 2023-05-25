package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.Session;
import chatweb.model.Message;

public class MessageRepository {
    private final Database database;
    private final ListMapper<Message> mapper = rs -> new Message(
            rs.getString("id"),
            rs.getString("message"),
            rs.getString("username"),
            null
    );

    public MessageRepository(Database database) {
        this.database = database;
    }

    public Message getRepliedMessage(String repliedMessageId) {
        if (repliedMessageId != null) {
            return database.executeSelect(mapper, "select * from messages where id = ?", repliedMessageId).stream()
                    .filter(message -> message.getId().equals(repliedMessageId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void saveMessage(Message message) {
        if (message.getRepliedMessage() != null) {
            database.execute("insert into messages (message, username, id, replied_message_id) values (?,?,?,?)",
                    message.getMessage(),
                    message.getUsername(),
                    message.getId(),
                    message.getRepliedMessage().getId());
        } else {
            database.execute("insert into messages (message, username, id, replied_message_id) values (?,?,?,?)",
                    message.getMessage(),
                    message.getUsername(),
                    message.getId(),
                    null);
        }
    }


}
