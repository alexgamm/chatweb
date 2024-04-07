package chatweb.action.executor;

import chatweb.action.ChangeTurn;
import chatweb.action.GameActionExecutionResult;
import chatweb.action.StartGame;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static chatweb.action.GameActionExecutionResult.PostTask.scheduled;

@Component
public class StartGameExecutor implements GameActionExecutor<StartGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, StartGame action) throws IllegalStateException {
        Turn turn = state.getTurn();
        if (turn == null) {
            throw new IllegalStateException("The game is not created");
        }
        int remainingTurnSeconds = turn.getDurationSeconds();
        if (turn.getPausedAt() != null) {
            int spentTurnSeconds = (int) Duration.between(turn.getTimeoutAt(), turn.getPausedAt()).getSeconds();
            remainingTurnSeconds -= spentTurnSeconds;
        }
        Turn newTurn = turn.toBuilder()
                .timeoutAt(Instant.now().plusSeconds(remainingTurnSeconds))
                .pausedAt(null)
                .build();
        return GameActionExecutionResult.builder()
                .newState(
                        state.copy()
                                .status(Status.ACTIVE)
                                .turn(newTurn)
                                .build()
                )
                .cancelScheduledTask(true)
                .postTasks(List.of(scheduled(
                        new ChangeTurn(action.getTurnSeconds()),
                        newTurn.getTimeoutAt()
                )))
                .build();
    }
}
