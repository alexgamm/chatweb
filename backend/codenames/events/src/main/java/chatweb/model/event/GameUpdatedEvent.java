package chatweb.model.event;

import chatweb.model.api.GameDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdatedEvent extends RoomEvent {
    private GameDto game;
}
