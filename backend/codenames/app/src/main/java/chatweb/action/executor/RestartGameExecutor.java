package chatweb.action.executor;

import chatweb.action.RestartGame;
import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import chatweb.entity.Team;
import chatweb.model.game.GameState;
import chatweb.model.game.Settings;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import chatweb.utils.GameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestartGameExecutor implements GameActionExecutor<RestartGame> {

    @Override
    public GameActionExecutionResult execute(GameState state, RestartGame action) throws IllegalStateException {
        return GameActionExecutionResult.builder()
                .newState(createState(action.getGame(), action.getDictionary()))
                .cancelActiveTasks(true)
                .build();
    }

    private GameState createState(Game game, Dictionary dictionary) {
        List<Integer> shuffledTeamIds = game.getShuffledTeams().stream().map(Team::getId).toList();
        Settings settings = game.getSettings();
        List<Card> cards = GameUtils.createCards(
                dictionary.getRandomWords(settings.getBoardSize().getWordAmount()),
                shuffledTeamIds
        );
        Turn turn = new Turn(
                shuffledTeamIds.get(0),
                true,
                settings.getTurnSeconds() * 2,
                null
        );
        return new GameState(Status.IDLE, cards, shuffledTeamIds, turn);
    }
}
