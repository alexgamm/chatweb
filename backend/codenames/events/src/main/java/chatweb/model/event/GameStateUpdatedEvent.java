package chatweb.model.event;

import org.jetbrains.annotations.NotNull;

public class GameStateUpdatedEvent extends RoomEvent {
    public GameStateUpdatedEvent(@NotNull String room) {
        super(room);
    }
}
