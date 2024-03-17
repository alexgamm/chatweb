package chatweb.action.executor;

import chatweb.action.StartGame;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StartGameExecutor implements GameActionExecutor<StartGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, StartGame action) throws IllegalStateException {
        Turn turn = state.getTurn();
        if (turn == null) {
            throw new IllegalStateException("The game is not created");
        }
        return GameActionExecutionResult.builder()
                .newState(
                        state.copy()
                                .status(Status.ACTIVE)
                                .turn(turn.toBuilder().startedAt(Instant.now()).build())
                                .build()
                )
                .cancelActiveTasks(true)
                .build();
    }
}
