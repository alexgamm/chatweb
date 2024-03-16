package chatweb.utils.updaters;

import chatweb.model.game.GameState;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class PauseGameExecutor implements GameActionExecutor<PauseGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, PauseGame action) throws IllegalStateException {
        Instant startedAt = state.getTurn().getStartedAt();
        if (startedAt == null) {
            throw new IllegalArgumentException("Cannot pause the game that is not started yet");
        }
        Integer durationSeconds = state.getTurn().getDurationSeconds();
        int remainingTime = (int) Instant.now().until(startedAt.plusSeconds(durationSeconds), ChronoUnit.SECONDS);
        return GameActionExecutionResult.builder()
                .newState(state.copy().turn(state.getTurn().toBuilder().durationSeconds(remainingTime).build()).build())
                .cancelActiveTasks(true)
                .build();
    }
}
