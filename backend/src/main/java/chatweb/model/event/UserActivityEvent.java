package chatweb.model.event;


import java.util.Date;

public class UserActivityEvent extends Event {
    private static final long LIFETIME = 20_000;
    private final String username;

    public UserActivityEvent(String username) {
        super(EventType.USER_ACTIVITY, new Date());
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - getDate().getTime() > LIFETIME;
    }
}
