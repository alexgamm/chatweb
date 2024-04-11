package chatweb.mapper;

import chatweb.entity.Team;
import chatweb.model.api.TeamDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = MemberMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface TeamMapper {

    TeamDto teamToTeamDto(Team team);
}
