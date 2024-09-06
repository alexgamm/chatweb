package chatweb.model.event;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class GameStateUpdatedEvent extends RoomEvent {
    public GameStateUpdatedEvent(@NotNull String room) {
        super(room);
    }
}
