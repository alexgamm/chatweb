package chatweb.model.event;

import chatweb.model.api.GameDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameUpdatedEvent extends RoomEvent {
    private GameDto game;

    public GameUpdatedEvent(GameDto game) {
        super(game.id());
        this.game = game;
    }
}
