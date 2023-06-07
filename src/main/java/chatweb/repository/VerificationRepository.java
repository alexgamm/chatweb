package chatweb.repository;

import chatweb.db.Database;
import chatweb.db.mappers.ListMapper;
import chatweb.entity.Verification;

public class VerificationRepository {
    private final Database database;
    private final ListMapper<Verification> mapper = rs -> new Verification(
            rs.getInt("user_id"),
            rs.getString("code"),
            rs.getBoolean("verified")
    );

    public VerificationRepository(Database database) {
        this.database = database;
    }

    public void createVerification(int userId, String code) {
        database.execute("insert into verifications (user_id, code) values (?,?)", userId, code);
    }

    public Verification findVerification(int userId) {
        return database.executeSelect(mapper, "select * from verifications where user_id = ?", userId).stream()
                .findFirst()
                .orElse(null);
    }
}
