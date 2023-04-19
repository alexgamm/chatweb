package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.User;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

public class UserRepository {
    private final Database database;
    private final ListMapper<User> mapper = rs -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getTimestamp("last_activity_at")
    );

    public UserRepository(Database database) {
        this.database = database;
    }

    public List<User> getAllUsers() {
        try {
            return database.executeSelect(mapper, "select * from users order by last_activity_at desc");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByUsername(@Nullable String username) {
        try {
            return database.executeSelect(mapper, "select * from users where username = ?", username)
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(int id) {
        try {
            return database.executeSelect(mapper, "select * from users where id = ?", id)
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(User user) {
        try {
            database.execute(
                    "insert into users (username, password, last_activity_at) values (?,?,?)",
                    user.getUsername(),
                    user.getPassword(),
                    user.getLastActivityAt()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLastActivityAt(int userId) {
        try {
            database.execute("update users set last_activity_at = now() where id = ?", userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
