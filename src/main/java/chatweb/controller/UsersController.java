package chatweb.controller;

import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.UserMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.UserListResponse;
import chatweb.model.dto.UserDto;
import chatweb.model.event.ChangeUsernameEvent;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsersController implements ApiControllerHelper {
    private final UserRepository userRepository;
    private final EventsService eventsService;

    @GetMapping
    public UserListResponse users() {
        List<UserListResponse.User> list = userRepository.findAll().stream()
                .map(user -> new UserListResponse.User(
                        user.getId(),
                        user.getUsername(),
                        user.getLastActivityAt(),
                        eventsService.hasEmitters(user.getId())
                ))
                .toList();
        return new UserListResponse(list);
    }

    @GetMapping("me")
    public UserDto getMe(@RequestAttribute User user) {
        return UserMapper.userToUserDto(user);
    }

    @PatchMapping
    @Transactional
    public UserDto changeUsername(@RequestBody User input, @RequestAttribute User user) throws ApiErrorException {
        if (input.getUsername().equals("")) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "invalid username"));
        }
        if (userRepository.findUserByUsername(input.getUsername()) != null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "username is already taken"));
        }
        userRepository.updateUsername(user.getId(), input.getUsername());
        user = userRepository.findUserById(user.getId());
        eventsService.addEvent(new ChangeUsernameEvent(user.getId(), user.getUsername()));
        return UserMapper.userToUserDto(user);
    }

}
