package chatweb.model.event;

import chatweb.model.dto.UserDto;
import lombok.Getter;

import java.util.Date;

@Getter
public class UserTypingEvent extends Event {
    private final int userId;

    public UserTypingEvent(int userId) {
        super(EventType.USER_TYPING, new Date());
        this.userId = userId;
    }
}
