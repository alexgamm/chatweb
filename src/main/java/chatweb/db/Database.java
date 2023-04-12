package chatweb.db;

import chatweb.db.mappers.ListMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Database {
    private final Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public <T> List<T> executeSelect(ListMapper<T> mapper, String sql, Object... params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int paramIdx = 0; paramIdx < params.length; paramIdx++) {
            ps.setObject(paramIdx + 1, params[paramIdx]);
        }
        // выполняет запрос и возвр. рез. из базы в виде rs
        return mapper.map(ps.executeQuery());
    }

    public void executeInsert(String sql, Object... params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int paramIdx = 0; paramIdx < params.length; paramIdx++) {
            ps.setObject(paramIdx + 1, params[paramIdx]);
        }
        //выполняет наш (sql) запрос
        ps.execute();
    }
}
