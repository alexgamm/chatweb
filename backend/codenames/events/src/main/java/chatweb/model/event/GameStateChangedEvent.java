package chatweb.model.event;

import chatweb.model.game.GameState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GameStateChangedEvent extends RoomEvent {
    private final String gameId;
    private final GameState state;
}
