package chatweb.action.executor;

import chatweb.action.GameActionExecutionResult;
import chatweb.action.ResetGame;
import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import chatweb.entity.Team;
import chatweb.model.game.GameState;
import chatweb.model.game.Settings;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import chatweb.repository.DictionaryRepository;
import chatweb.utils.GameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResetGameExecutor implements GameActionExecutor<ResetGame> {

    private final DictionaryRepository dictionaryRepository;

    @Override
    public GameActionExecutionResult execute(GameState state, ResetGame action) throws IllegalStateException {
        Dictionary dictionary = dictionaryRepository
                .findById(action.getGame().getSettings().dictionaryId())
                .orElse(null);
        if (dictionary == null) {
            throw new IllegalStateException("Dictionary is not found");
        }
        return GameActionExecutionResult.builder()
                .newState(createState(action.getGame(), dictionary))
                .cancelScheduledTask(true)
                .build();
    }

    private GameState createState(Game game, Dictionary dictionary) {
        List<Integer> shuffledTeamIds = game.getShuffledTeams().stream().map(Team::getId).toList();
        Settings settings = game.getSettings();
        List<Card> cards = GameUtils.createCards(
                dictionary.getRandomWords(settings.boardSize().getWordAmount()),
                shuffledTeamIds
        );
        int firstTurnDurationSeconds = settings.turnSeconds() * 2;
        Turn turn = Turn.builder()
                .teamId(shuffledTeamIds.get(0))
                .leader(true)
                .durationSeconds(firstTurnDurationSeconds)
                .build();
        return GameState.builder()
                .status(Status.IDLE)
                .cards(cards)
                .turnOrderTeamIds(shuffledTeamIds)
                .turn(turn)
                .build();
    }
}
