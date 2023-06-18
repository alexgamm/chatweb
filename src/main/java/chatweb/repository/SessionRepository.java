package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.Session;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SessionRepository {
    private final Database database;
    private final ListMapper<Session> mapper = rs -> new Session(
            rs.getString("id"),
            rs.getInt("user_id")
    );

    public SessionRepository(Database database) {
        this.database = database;
    }

    public void saveSession(Session session) {
        database.execute("insert into sessions (id, user_id) values (?,?)", session.getId(), session.getUserId());
    }

    public Session findSessionById(@Nullable String id) {
        if (id == null) {
            return null;
        }
        return database.executeSelect(mapper, "select * from sessions where id = ?", id).stream()
                .filter(session -> session.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
