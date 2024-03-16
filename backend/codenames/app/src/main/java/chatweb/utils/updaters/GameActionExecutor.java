package chatweb.utils.updaters;

import chatweb.model.game.GameState;

public interface GameActionExecutor<T extends GameAction> {
    GameActionExecutionResult execute(GameState state, T action) throws IllegalStateException;
}
