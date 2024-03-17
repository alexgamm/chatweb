package chatweb.action.executor;

import chatweb.action.ChangeTurn;
import chatweb.action.EndGame;
import chatweb.action.PickCard;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static chatweb.action.executor.GameActionExecutionResult.PostTask.immediate;

@Component
@RequiredArgsConstructor
public class PickCardExecutor implements GameActionExecutor<PickCard> {

    @Override
    public GameActionExecutionResult execute(GameState state, PickCard action) throws IllegalStateException {
        Turn turn = state.getTurn();
        Card pickedCard = state.getCards().stream()
                .filter(card -> card.getWord().equals(action.getWord()))
                .findFirst().orElse(null);
        if (turn == null) {
            throw new IllegalStateException("The game is not created");
        }
        if (!turn.getTeamId().equals(action.getPickedTeamId())) {
            throw new IllegalStateException("It's not your team's turn");
        }
        if (state.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException("Game is stopped");
        }
        if (pickedCard == null) {
            throw new IllegalStateException("No such word on the game board");
        }
        if (pickedCard.getPickedByTeamId() != null) {
            throw new IllegalStateException("The card is already picked");
        }
        List<Card> newCards = state.getCards().stream().map(card -> {
            Card.CardBuilder copy = card.toBuilder();
            if (card == pickedCard) {
                copy = copy.pickedByTeamId(action.getPickedTeamId());
            }
            return copy.build();
        }).collect(Collectors.toList());
        state = state.copy().cards(newCards).build();
        List<GameActionExecutionResult.PostTask> postTasks =
                switch (pickedCard.getType()) {
                    case BLACK -> List.of(immediate(
                            new EndGame(action.getPickedTeamId(), null)
                    ));
                    case NEUTRAL -> List.of(immediate(
                            new ChangeTurn(action.getTurnSeconds())
                    ));
                    case COLOR -> {
                        if (state.allCardsPicked(pickedCard.getTeamId())) {
                            yield List.of(immediate(
                                    new EndGame(null, pickedCard.getTeamId())
                            ));
                        }
                        if (!action.getPickedTeamId().equals(pickedCard.getTeamId())) {
                            yield List.of(immediate(
                                    new ChangeTurn(action.getTurnSeconds())
                            ));
                        }
                        yield Collections.emptyList();
                    }
                };
        return GameActionExecutionResult.builder()
                .newState(state)
                .cancelScheduledTasks(!postTasks.isEmpty())
                .postTasks(postTasks)
                .build();
    }

}
