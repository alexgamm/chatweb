package chatweb.action.executor;

import chatweb.action.ChangeTurn;
import chatweb.action.GameActionExecutionResult;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Turn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static chatweb.action.GameActionExecutionResult.PostTask.scheduled;

@Component
@RequiredArgsConstructor
public class ChangeTurnExecutor implements GameActionExecutor<ChangeTurn> {

    @Override
    public GameActionExecutionResult execute(GameState state, ChangeTurn action) throws IllegalStateException {
        // change stage from thinking of to picking
        Turn.TurnBuilder newTurn = state.getTurn()
                .toBuilder()
                .durationSeconds(action.getTurnSeconds())
                .timeoutAt(Instant.now().plusSeconds(action.getTurnSeconds()));
        if (state.getTurn().isLeader()) {
            newTurn = newTurn.leader(false);
        } else {
            // change teams turn
            List<Integer> teamIdsOrder = state.getTurnOrderTeamIds();
            int currentTeamIndex = teamIdsOrder.indexOf(state.getTurn().getTeamId());
            int nextTeamIndex = (currentTeamIndex + 1) % teamIdsOrder.size();
            Integer nextTeamId = teamIdsOrder.get(nextTeamIndex);
            newTurn = newTurn.leader(true).teamId(nextTeamId);
        }
        state = state.copy().turn(newTurn.build()).build();
        return GameActionExecutionResult.builder()
                .newState(state)
                .cancelScheduledTask(true)
                .postTasks(List.of(scheduled(
                        new ChangeTurn(action.getTurnSeconds()),
                        Objects.requireNonNull(state.getTurn().getTimeoutAt())
                )))
                .build();
    }
}
