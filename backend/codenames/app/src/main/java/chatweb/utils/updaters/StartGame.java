package chatweb.utils.updaters;

import chatweb.model.game.state.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StartGame implements GameAction {
    private final Status gameStateStatus;
}
