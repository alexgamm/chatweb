package chatweb.model.game;

import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Turn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class GameState {
    private Status status;
    private List<Card> cards;
    private List<Integer> turnOrderTeamIds;
    private Turn turn;
    private Map<Integer, Long> remainingCards; // Map<teamId, number of remaining cards>
    private Set<Integer> lostTeamIds;

    public GameStateBuilder copy() {
        return toBuilder()
                .cards(Collections.unmodifiableList(cards))
                .turnOrderTeamIds(Collections.unmodifiableList(turnOrderTeamIds))
                .turn(turn.toBuilder().build())
                .lostTeamIds(Collections.unmodifiableSet(lostTeamIds));
    }

    public boolean allCardsPicked(Integer teamId) {
        return this.getCards().stream()
                .filter(card -> teamId.equals(card.getTeamId()))
                .map(Card::getPickedByTeamId)
                .allMatch(Objects::nonNull);
    }
}
