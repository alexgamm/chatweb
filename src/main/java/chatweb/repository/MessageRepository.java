package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.User;
import chatweb.model.Message;
import chatweb.model.MessagesResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageRepository {
    private final Database database;
    private final ListMapper<Message> mapper = rs -> new Message(
            rs.getString("id"),
            rs.getString("message"),
            rs.getString("username"),
            getMessage(rs.getString("replied_message_id")),
            rs.getTimestamp("send_date"));

    public MessageRepository(Database database) {
        this.database = database;
    }

    public Message getMessage(String repliedMessageId) {
        if (repliedMessageId == null) {
            return null;
        }
        return database.executeSelect(mapper, "select * from messages where id = ?", repliedMessageId).stream()
                .findFirst()
                .orElse(null);
    }

    public List<Message> getAllMessages() {
        return database.executeSelect(mapper, "select * from messages order by send_date desc");
    }

    public List<Message> getSeveralMessages(int count) {
        return database.executeSelect(mapper, "select * from messages order by send_date desc limit ?", count);

    }
    public List<Message> getSeveralMessagesFrom(int count, Date date){
        return database.executeSelect(mapper, "select * from messages where send_date < ? order by send_date desc limit ?", Timestamp.from(date.toInstant()), count);
    }



    public void saveMessage(Message message) {
        database.execute(
                "insert into messages (message, username, id, replied_message_id, send_date) values (?,?,?,?,?)",
                message.getMessage(),
                message.getUsername(),
                message.getId(),
                message.getRepliedMessage() == null ? null : message.getRepliedMessage().getId(),
                message.getSendDate()
        );
    }
}
