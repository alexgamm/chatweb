package chatweb.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserTypingEvent extends UserEvent {
    public UserTypingEvent(int userId) {
        super(userId);
    }
}
