package chatweb.action.executor;

import chatweb.action.GameActionExecutionResult;
import chatweb.action.PauseGame;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Status;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class PauseGameExecutor implements GameActionExecutor<PauseGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, PauseGame action) throws IllegalStateException {
        Instant startedAt = state.getTurn().getStartedAt();
        if (startedAt == null) {
            throw new IllegalStateException("Cannot pause the game that is not started yet");
        }
        Integer durationSeconds = state.getTurn().getDurationSeconds();
        int remainingTime = (int) Instant.now().until(startedAt.plusSeconds(durationSeconds), ChronoUnit.SECONDS);
        GameState newState = state.copy()
                .status(Status.PAUSED)
                .turn(state.getTurn().toBuilder().durationSeconds(remainingTime).build())
                .build();
        return GameActionExecutionResult.builder()
                .newState(newState)
                .cancelScheduledTask(true)
                .build();
    }
}
