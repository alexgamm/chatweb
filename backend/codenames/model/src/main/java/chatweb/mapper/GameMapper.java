package chatweb.mapper;

import chatweb.entity.Game;
import chatweb.model.api.GameDto;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Mapper(uses = {TeamMapper.class, MemberMapper.class})
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(target = "state", expression = "java(mapState(userId, game))")
    GameDto map(Integer userId, Game game);

    @SuppressWarnings("unused")
    default GameState mapState(Integer userId, Game game) {
        GameState state = game.getState();
        return state.copy()
                .cards(mapCards(userId, game))
                .remainingCards(countRemainingCards(state.getCards()))
                .build();
    }

    private static List<Card> mapCards(Integer userId, Game game) {
        boolean areCardsRevealed = game.getState().getStatus() == Status.FINISHED
                || game.getTeams().stream().anyMatch(team -> team.isLeader(userId));
        return game.getState().getCards().stream()
                .map(card -> new Card(
                        areCardsRevealed || card.getPickedByTeamId() != null ? card.getType() : null,
                        card.getWord(),
                        areCardsRevealed ? card.getTeamId() : null,
                        card.getPickedByTeamId()
                ))
                .toList();
    }

    private static Map<Integer, Long> countRemainingCards(List<Card> cards) {
        return cards.stream()
                .filter(card -> card.getTeamId() != null && card.getPickedByTeamId() == null)
                .collect(Collectors.groupingBy(Card::getTeamId, Collectors.counting()));
    }
}
