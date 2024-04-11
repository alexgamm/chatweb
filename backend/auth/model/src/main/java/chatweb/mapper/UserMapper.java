package chatweb.mapper;

import chatweb.entity.User;
import chatweb.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "hasPassword", expression = "java(user.getPassword() != null)")
    UserDto toDto(User user);
}
