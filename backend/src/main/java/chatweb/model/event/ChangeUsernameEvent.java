package chatweb.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
public class ChangeUsernameEvent extends Event {
    private final int userId;
    private final String newUsername;

    public ChangeUsernameEvent(int userId, String newUsername) {
        super(EventType.CHANGE_USERNAME, new Date());
        this.userId = userId;
        this.newUsername = newUsername;
    }
}
