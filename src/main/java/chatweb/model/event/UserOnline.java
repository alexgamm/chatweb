package chatweb.model.event;


import lombok.Getter;

import java.util.Date;

@Getter
public class UserOnline extends Event {
    private final int userId;
    private final boolean online;

    public UserOnline(int userId, boolean online) {
        super(EventType.USER_ONLINE, new Date());
        this.userId = userId;
        this.online = online;
    }
}
