package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.Session;
import chatweb.entity.User;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private final Database database;
    private final ListMapper<Session> mapper = rs -> new Session(rs.getString("id"), rs.getInt("userId"));

    public SessionRepository(Database database) {
        this.database = database;
    }

    public void saveSession(Session session) {
        try {
            database.executeInsert("insert into sessions (id, userId) values (?,?)",  session.getId(), session.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Session findSessionById(@Nullable String id)  {
        if (id == null) {
            return null;
        }
        try {
            return database.executeSelect(mapper,"select * from sessions where id = ?", id).stream()
                    .filter(session -> session.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
