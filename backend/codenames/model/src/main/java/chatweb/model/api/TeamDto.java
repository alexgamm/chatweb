package chatweb.model.api;

import chatweb.model.dto.UserDto;
import chatweb.model.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
@AllArgsConstructor
@Getter
public class TeamDto {
    private Integer id;
    private Set<UserDto> players;
    private Color color;
    private UserDto leader;
}
