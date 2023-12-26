package chatweb.model.event;


import lombok.Getter;

import java.util.Date;

@Getter
public class UserOnlineEvent extends Event {
    private int userId;
    private boolean online;

    public UserOnlineEvent() {
        super(EventType.USER_ONLINE, null, null);
    }

    public UserOnlineEvent(int userId, boolean online) {
        super(EventType.USER_ONLINE, new Date(), null);
        this.userId = userId;
        this.online = online;
    }
}
