package chatweb.mapper;

import chatweb.entity.User;
import chatweb.model.dto.UserDto;

public class UserMapper {
    public static UserDto userToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword() != null,
                user.getColor()
        );
    }
}
