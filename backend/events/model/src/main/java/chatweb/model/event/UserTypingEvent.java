package chatweb.model.event;

import lombok.Getter;

import java.util.Date;

@Getter
public class UserTypingEvent extends Event {
    private int userId;

    public UserTypingEvent() {
        super(EventType.USER_TYPING, null);
    }

    public UserTypingEvent(int userId) {
        super(EventType.USER_TYPING, new Date());
        this.userId = userId;
    }
}
