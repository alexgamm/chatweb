package chatweb.model.event;

import chatweb.model.user.UserColor;
import lombok.Getter;

import java.util.Date;

@Getter
public class ChangeUserColorEvent extends Event {
    private int userId;
    private UserColor color;

    public ChangeUserColorEvent() {
        super(EventType.CHANGE_COLOR, null);
    }

    public ChangeUserColorEvent(int userId, UserColor color) {
        super(EventType.CHANGE_COLOR, new Date());
        this.userId = userId;
        this.color = color;
    }
}
