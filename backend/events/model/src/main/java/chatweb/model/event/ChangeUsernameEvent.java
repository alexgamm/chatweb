package chatweb.model.event;

import lombok.Getter;

import java.util.Date;

@Getter
public class ChangeUsernameEvent extends Event {
    private int userId;
    private String newUsername;

    public ChangeUsernameEvent() {
        super(EventType.CHANGE_USERNAME, null, null);
    }

    public ChangeUsernameEvent(int userId, String newUsername) {
        super(EventType.CHANGE_USERNAME, new Date(), null);
        this.userId = userId;
        this.newUsername = newUsername;
    }
}
