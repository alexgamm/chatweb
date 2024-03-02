package chatweb.mapper;

import chatweb.entity.Team;
import chatweb.model.api.TeamDto;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TeamMapper {
    public TeamDto map(Team team) {
        return new TeamDto(
                team.getId(),
                team.getPlayers().stream().map(UserMapper::userToUserDto).collect(Collectors.toSet()),
                team.getColor(),
                Optional.ofNullable(team.getLeader()).map(UserMapper::userToUserDto).orElse(null)
        );
    }
}
