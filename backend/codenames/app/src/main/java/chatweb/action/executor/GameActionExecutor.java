package chatweb.action.executor;

import chatweb.action.GameAction;
import chatweb.action.GameActionExecutionResult;
import chatweb.model.game.GameState;

public interface GameActionExecutor<T extends GameAction> {
    GameActionExecutionResult execute(GameState state, T action) throws IllegalStateException;
}
