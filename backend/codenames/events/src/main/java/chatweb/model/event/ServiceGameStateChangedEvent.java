package chatweb.model.event;

import chatweb.entity.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServiceGameStateChangedEvent extends RoomEvent {
    private final Game game;
}
