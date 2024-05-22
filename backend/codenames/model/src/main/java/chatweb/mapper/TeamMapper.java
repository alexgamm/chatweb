package chatweb.mapper;

import chatweb.entity.Team;
import chatweb.model.api.TeamDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = MemberMapper.class)
public interface TeamMapper {
    TeamMapper INSTANCE = Mappers.getMapper(TeamMapper.class);

    @SuppressWarnings("unused")
    TeamDto toDto(Team team);
}
