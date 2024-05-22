package chatweb.action.executor;

import chatweb.action.EndGame;
import chatweb.action.GameActionExecutionResult;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Status;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EndGameExecutor implements GameActionExecutor<EndGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, EndGame action) throws IllegalStateException {
        Set<Integer> lostTeamIds = Optional.ofNullable(action.getLostTeamId())
                .map(Collections::singleton)
                .or(() -> Optional.ofNullable(action.getWonTeamId())
                        .map(wonTeamId -> state.getTurnOrderTeamIds().stream()
                                .filter(id -> !Objects.equals(id, wonTeamId))
                                .collect(Collectors.toSet())
                        )
                )
                .orElseThrow(() -> new IllegalStateException("Need wonTeamId or lostTeamId"));
        return GameActionExecutionResult.builder()
                .newState(state.copy().status(Status.FINISHED).lostTeamIds(lostTeamIds).build())
                .cancelScheduledTask(true)
                .build();
    }
}
