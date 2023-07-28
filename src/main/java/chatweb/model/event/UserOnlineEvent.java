package chatweb.model.event;


import lombok.Getter;

import java.util.Date;

@Getter
public class UserOnlineEvent extends Event {
    private final int userId;
    private final boolean online;

    public UserOnlineEvent(int userId, boolean online) {
        super(EventType.USER_ONLINE, new Date());
        this.userId = userId;
        this.online = online;
    }
}
