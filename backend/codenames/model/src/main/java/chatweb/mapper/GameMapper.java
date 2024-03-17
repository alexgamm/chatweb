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
    private final TeamMapper teamMapper;

    public GameDto map(Integer userId, Game game) {
        return new GameDto(
                game.getId(),
                UserMapper.userToUserDto(game.getHost()),
                game.getViewers().stream().map(UserMapper::userToUserDto).collect(Collectors.toSet()),
                game.getTeams().stream().map(teamMapper::map).toList(),
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
