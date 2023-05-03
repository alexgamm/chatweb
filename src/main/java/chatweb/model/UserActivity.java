package chatweb.model;


public class UserActivity extends Event {
    private static final long LIFETIME = 20_000;
    private final String username;

    public UserActivity(String username) {
        super(EventType.USER_ACTIVITY);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - getDate().getTime() > LIFETIME;
    }
}
