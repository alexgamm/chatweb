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

    @Mapping(target = "state", expression = "java(toPersonalState(userId, game))")
    GameDto toPersonalDto(Integer userId, Game game);

    @SuppressWarnings("unused")
    @Mapping(target = ".", source = "game.state")
    @Mapping(target = "cards", expression = "java(toPersonalCards(userId, game))")
    @Mapping(target = "remainingCards", expression = "java(countRemainingCards(game.getState().getCards()))")
    GameState toPersonalState(Integer userId, Game game);

    @SuppressWarnings("unused")
    default List<Card> toPersonalCards(Integer userId, Game game) {
        boolean areCardsRevealed = game.getState().getStatus() == Status.FINISHED
                || game.getTeams().stream().anyMatch(team -> team.isLeader(userId));
        return game.getState().getCards().stream()
                .map(card -> {
                    boolean isRevealed = areCardsRevealed || card.getPickedByTeamId() != null;
                    return new Card(
                            isRevealed ? card.getType() : null,
                            card.getWord(),
                            isRevealed ? card.getTeamId() : null,
                            card.getPickedByTeamId()
                    );
                })
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
