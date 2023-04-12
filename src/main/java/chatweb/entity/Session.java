package chatweb.entity;

public class Session {
    private final String id;
    private final int userId;

    public Session(String id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }
}
