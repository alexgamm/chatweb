package chatweb.model.api;

import chatweb.model.dto.UserDto;
import chatweb.model.game.GameState;
import chatweb.model.game.Settings;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class GameDto {
    private String id;
    private UserDto host;
    private Set<UserDto> viewers;
    private List<TeamDto> teams;
    private Settings settings;
    private GameState state;
}
