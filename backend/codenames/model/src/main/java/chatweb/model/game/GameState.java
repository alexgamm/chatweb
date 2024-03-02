package chatweb.model.game;

import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import chatweb.model.game.state.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
@Getter
public class GameState {
    private Status status;
    private List<Card> cards;
    private List<Integer> turnOrderTeamIds;
    @Nullable
    private Turn turn;
}
