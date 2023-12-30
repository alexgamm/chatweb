package chatweb.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeUsernameEvent extends UserEvent {
    private String newUsername;

    public ChangeUsernameEvent(int userId, String newUsername) {
        super(userId);
        this.newUsername = newUsername;
    }
}
