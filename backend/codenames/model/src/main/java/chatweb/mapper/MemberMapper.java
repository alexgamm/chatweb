package chatweb.mapper;

import chatweb.entity.User;
import chatweb.model.game.Member;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface MemberMapper {

    @Mapping(source = "id", target = "userId")
    Member userToMember(User user);
}
