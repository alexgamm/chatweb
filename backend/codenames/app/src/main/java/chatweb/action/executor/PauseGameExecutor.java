package chatweb.action.executor;

import chatweb.action.GameActionExecutionResult;
import chatweb.action.PauseGame;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Status;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PauseGameExecutor implements GameActionExecutor<PauseGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, PauseGame action) throws IllegalStateException {
        GameState newState = state.copy()
                .status(Status.PAUSED)
                .turn(state.getTurn().toBuilder().pausedAt(Instant.now()).build())
                .build();
        return GameActionExecutionResult.builder()
                .newState(newState)
                .cancelScheduledTask(true)
                .build();
    }
}
