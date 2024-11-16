package chatweb.mapper;

import chatweb.entity.Game;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Mapper
public interface GameStateMapper {
    GameStateMapper INSTANCE = Mappers.getMapper(GameStateMapper.class);

    @SuppressWarnings("unused")
    @Mapping(target = ".", source = "game.state")
    @Mapping(target = "cards", expression = "java(toPersonalCards(userId, game))")
    @Mapping(target = "remainingCards", expression = "java(countRemainingCards(game.getState().getCards()))")
    GameState toPersonalState(Integer userId, Game game);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "teamId", ignore = true)
    Card toUnrevealedCard(Card card);

    @SuppressWarnings("unused")
    default List<Card> toPersonalCards(Integer userId, Game game) {
        List<Card> cards = game.getState().getCards();
        boolean areCardsRevealed = game.getState().getStatus() == Status.FINISHED
                || game.getTeams().stream().anyMatch(team -> team.isLeader(userId));
        if (areCardsRevealed) {
            return cards;
        }
        return cards.stream()
                .map(card -> card.getPickedByTeamId() == null ? toUnrevealedCard(card) : card)
                .toList();
    }

    @SuppressWarnings("unused")
    default Map<Integer, Long> countRemainingCards(List<Card> cards) {
        return cards.stream()
                .filter(card -> card.getTeamId() != null)
                .collect(Collectors.groupingBy(
                        Card::getTeamId,
                        Collectors.summingLong((card) -> card.getPickedByTeamId() == null ? 1 : 0))
                );
    }
}
