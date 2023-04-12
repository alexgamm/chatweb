package chatweb.db.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ListMapper<T> {
    default List<T> map(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapSingle(rs));
        }
        return list;
    }

    T mapSingle(ResultSet rs) throws SQLException;

}
