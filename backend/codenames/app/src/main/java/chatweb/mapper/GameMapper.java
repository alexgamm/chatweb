package chatweb.mapper;

import chatweb.entity.Game;
import chatweb.entity.User;
import chatweb.model.api.GameDto;
import chatweb.model.game.GameState;
import chatweb.model.game.state.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class GameMapper {
    private final TeamMapper teamMapper;

    public GameDto map(User user, Game game) {
        return new GameDto(
                game.getId(),
                UserMapper.userToUserDto(game.getHost()),
                game.getViewers().stream().map(UserMapper::userToUserDto).collect(Collectors.toSet()),
                game.getTeams().stream().map(teamMapper::map).toList(),
                game.getSettings(),
                mapState(user, game)
        );
    }

    public GameState mapState(User user, Game game) {
        GameState state = game.getState();
        boolean isLeader = game.getTeams().stream().anyMatch(team -> team.isLeader(user));
        return new GameState(
                state.getStatus(),
                state.getCards().stream()
                        .map(card -> new Card(
                                isLeader ? card.getType() : null,
                                card.getWord(),
                                isLeader ? card.getTeamId() : null,
                                card.getPickedByTeamId()
                        ))
                        .toList(),
                state.getTurnOrderTeamIds(),
                state.getTurn()
        );
    }
}
