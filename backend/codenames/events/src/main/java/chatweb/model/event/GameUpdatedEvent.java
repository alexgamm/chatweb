package chatweb.model.event;

import chatweb.model.api.GameDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GameUpdatedEvent extends RoomEvent {
    private final GameDto game;
}
