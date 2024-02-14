package chatweb.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public class TypingEvent extends RoomEvent {
    private int userId;

    public TypingEvent(@Nullable String room, int userId) {
        super(room);
        this.userId = userId;
    }
}
