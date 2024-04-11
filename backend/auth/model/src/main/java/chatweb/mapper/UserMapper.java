package chatweb.mapper;

import chatweb.entity.User;
import chatweb.model.dto.UserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

    @Mapping(target = "hasPassword", expression = "java(user.getPassword() != null)")
    UserDto userToUserDto(User user);
}
