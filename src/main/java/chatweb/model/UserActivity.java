package chatweb.model;

import java.util.Date;

public class UserActivity extends Event{
    private final String username;
    private final Date lastActivityAt;

    public UserActivity(String username, Date lastActivityAt) {
        super(EventType.USER_ACTIVITY);
        this.username = username;
        this.lastActivityAt = lastActivityAt;
    }

    public String getUsername() {
        return username;
    }

    public Date getLastActivityAt() {
        return lastActivityAt;
    }
}
