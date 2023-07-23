package chatweb.db;

import chatweb.db.mappers.ListMapper;
import chatweb.exception.DatabaseException;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

//TODO remove
public class Database {
    private final Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public <T> List<T> executeSelect(ListMapper<T> mapper, String sql, Object... params) {
        // выполняет запрос и возвр. рез. из базы в виде rs
        try {
            return mapper.map(prepareStatement(sql, params).executeQuery());
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void execute(String sql, Object... params) {
        try {
            prepareStatement(sql, params).execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        //выполняет наш (sql) запрос

    }
    public PreparedStatement prepareStatement (String sql, Object... params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int paramIdx = 0; paramIdx < params.length; paramIdx++) {
            ps.setObject(paramIdx + 1, params[paramIdx]);
        }
        return ps;
    }
}
