package chatweb.utils.updaters;

import chatweb.model.game.GameState;
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
                        state.copy().turn(
                                turn.toBuilder()
                                        .teamId(turn.getTeamId())
                                        .leader(turn.isLeader())
                                        .durationSeconds(turn.getDurationSeconds())
                                        .startedAt(Instant.now()).build()
                        ).build()
                )
                .cancelActiveTasks(true)
                .build();
    }
}
