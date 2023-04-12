package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.User;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class UserRepository {
    private final Database database;
    private final ListMapper<User> mapper = rs -> new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));

    public UserRepository(Database database) {
        this.database = database;
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
            database.executeInsert("insert into users (username, password) values (?,?)", user.getUsername(), user.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
