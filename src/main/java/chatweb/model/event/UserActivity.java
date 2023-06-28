package chatweb.model.event;


import chatweb.model.event.Event;
import chatweb.model.event.EventType;

import java.util.Date;

public class UserActivity extends Event {
    private static final long LIFETIME = 20_000;
    private final String username;

    public UserActivity(String username) {
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
