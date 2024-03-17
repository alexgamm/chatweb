package chatweb.action;

import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RestartGame implements GameAction {
    private final Game game;
    private final Dictionary dictionary;
}
