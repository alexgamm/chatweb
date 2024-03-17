package chatweb.action.executor;

import chatweb.action.EndGame;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Status;
import org.springframework.stereotype.Component;

@Component
public class EndGameExecutor implements GameActionExecutor<EndGame> {
    @Override
    public GameActionExecutionResult execute(GameState state, EndGame action) throws IllegalStateException {
        return GameActionExecutionResult.builder()
                .newState(state.copy().status(Status.FINISHED).build())
                .cancelScheduledTasks(true)
                .build();
    }
}
