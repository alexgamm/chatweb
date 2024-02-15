package chatweb.model.event;

import chatweb.model.user.UserColor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeUserColorEvent extends UserEvent {
    private UserColor color;

    public ChangeUserColorEvent(int userId, UserColor color) {
        super(userId);
        this.color = color;
    }
}
