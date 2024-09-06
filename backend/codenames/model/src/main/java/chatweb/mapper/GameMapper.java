package chatweb.mapper;

import chatweb.entity.Game;
import chatweb.model.api.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {TeamMapper.class, MemberMapper.class})
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDto toDto(Game game);
}
