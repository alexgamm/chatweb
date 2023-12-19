package chatweb.model.event;


import lombok.Getter;

import java.util.Date;

@Getter
public class UserOnlineEvent extends Event {
    private int userId;
    private boolean online;

    public UserOnlineEvent() {
        super(EventType.USER_ONLINE, null);
    }

    public UserOnlineEvent(int userId, boolean online) {
        super(EventType.USER_ONLINE, new Date());
        this.userId = userId;
        this.online = online;
    }
}
