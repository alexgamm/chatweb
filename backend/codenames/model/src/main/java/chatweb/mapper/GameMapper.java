package chatweb.mapper;

import chatweb.entity.Game;
import chatweb.model.api.GameDto;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class GameMapper {

    public static GameDto map(Integer userId, Game game) {
        return new GameDto(
                game.getId(),
                MemberMapper.INSTANCE.userToMember(game.getHost()),
                game.getViewers().stream().map(MemberMapper.INSTANCE::userToMember).collect(Collectors.toSet()),
                game.getTeams().stream().map(TeamMapper.INSTANCE::teamToDtoMapper).toList(),
                game.getSettings(),
                mapState(userId, game)
        );
    }

    public static GameState mapState(Integer userId, Game game) {
        GameState state = game.getState();
        boolean areCardsRevealed = state.getStatus() == Status.FINISHED
                || game.getTeams().stream().anyMatch(team -> team.isLeader(userId));
        return new GameState(
                state.getStatus(),
                state.getCards().stream()
                        .map(card -> new Card(
                                areCardsRevealed || card.getPickedByTeamId() != null ? card.getType() : null,
                                card.getWord(),
                                areCardsRevealed ? card.getTeamId() : null,
                                card.getPickedByTeamId()
                        ))
                        .toList(),
                state.getTurnOrderTeamIds(),
                state.getTurn()
        );
    }
}
