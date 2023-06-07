package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.User;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.List;

public class UserRepository {
    private final Database database;
    private final ListMapper<User> mapper = rs -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getTimestamp("last_activity_at"));

    public UserRepository(Database database) {
        this.database = database;
    }

    public List<User> getAllUsers() {
        return database.executeSelect(mapper, "select * from users order by last_activity_at desc");
    }

    public User findUserByUsername(@Nullable String username) {
        return database.executeSelect(mapper, "select * from users where username = ?", username)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public User findUserById(int id) {
        return database.executeSelect(mapper, "select * from users where id = ?", id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public void saveUser(User user) {
        database.execute(
                "insert into users (username, password, last_activity_at, email) values (?,?,?,?)",
                user.getUsername(),
                user.getPassword(),
                new Timestamp(user.getLastActivityAt().getTime()),
                user.getEmail()
        );

    }

    public void updateLastActivityAt(int userId) {
        database.execute("update users set last_activity_at = now() where id = ?", userId);
    }
}
