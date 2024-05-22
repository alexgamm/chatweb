package chatweb.model.event;

import chatweb.model.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeUserColorEvent extends UserEvent {
    private Color color;

    public ChangeUserColorEvent(int userId, Color color) {
        super(userId);
        this.color = color;
    }
}
