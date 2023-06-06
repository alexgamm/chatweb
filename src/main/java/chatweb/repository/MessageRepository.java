package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.model.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageRepository {
    private final Database database;

    public MessageRepository(Database database) {
        this.database = database;
    }

    private ListMapper<Message> mapper(boolean includeRepliedMessage) {
        return rs -> new Message(
                rs.getString("id"),
                rs.getString("message"),
                rs.getString("username"),
                includeRepliedMessage ? getMessage(rs.getString("replied_message_id"), false) : null,
                rs.getTimestamp("send_date")
        );
    }

    public Message getMessage(String messageId, boolean includeRepliedMessage) {
        if (messageId == null) {
            return null;
        }
        return database.executeSelect(
                        mapper(includeRepliedMessage),
                        "select * from messages where id = ?",
                        messageId
                )
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<Message> getSeveralMessagesFrom(int count, Date from) {
        String query = "select * from messages";
        List<Object> params = new ArrayList<>();
        if (from != null) {
            query += " where send_date < ?";
            params.add(Timestamp.from(from.toInstant()));
        }
        query += " order by send_date desc limit ?";
        params.add(count);
        return database.executeSelect(mapper(true), query, params.toArray());
    }

    public void saveMessage(Message message) {
        database.execute(
                "insert into messages (message, username, id, replied_message_id, send_date) values (?,?,?,?,?)",
                message.getMessage(),
                message.getUsername(),
                message.getId(),
                message.getRepliedMessage() == null ? null : message.getRepliedMessage().getId(),
                Timestamp.from(message.getSendDate().toInstant())
        );
    }
}
