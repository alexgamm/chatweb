package chatweb.mapper;

import chatweb.entity.User;
import chatweb.model.game.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    @Mapping(source = "id", target = "userId")
    Member toMember(User user);
}
