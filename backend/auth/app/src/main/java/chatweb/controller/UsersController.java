package chatweb.controller;

import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.UserMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.ChangePasswordRequest;
import chatweb.model.api.EmptyResponse;
import chatweb.model.api.UserListResponse;
import chatweb.model.dto.UserDto;
import chatweb.model.event.ChangeUserColorEvent;
import chatweb.model.event.ChangeUsernameEvent;
import chatweb.producer.EventsKafkaProducer;
import chatweb.repository.UserRepository;
import chatweb.utils.PasswordUtils;
import chatweb.utils.RedisKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
public class UsersController implements ApiControllerHelper {
    private final UserRepository userRepository;
    private final EventsKafkaProducer eventsProducer;
    private final RedisTemplate<String, Integer> redisTemplate;

    @GetMapping
    public UserListResponse users() {
        // TODO introduce pagination
        Set<Integer> onlineUserIds = redisTemplate.opsForSet().members(RedisKeys.ONLINE_USER_IDS);
        List<UserListResponse.User> list = userRepository.findAll().stream()
                .map(user -> new UserListResponse.User(
                        user.getId(),
                        user.getUsername(),
                        onlineUserIds != null && onlineUserIds.contains(user.getId()),
                        user.getColor()
                ))
                .toList();
        return new UserListResponse(list);
    }

    @GetMapping("me")
    public UserDto getMe(@RequestAttribute User user) {
        return UserMapper.INSTANCE.toDto(user);
    }

    @PutMapping("me/username")
    @Transactional
    public UserDto changeUsername(@RequestBody UserDto input, @RequestAttribute User user) throws ApiErrorException {
        if (input.getUsername().isEmpty()) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "invalid username"));
        }
        if (userRepository.findUserByUsername(input.getUsername()) != null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "username is already taken"));
        }
        userRepository.updateUsername(user.getId(), input.getUsername());
        user = userRepository.findUserById(user.getId());
        eventsProducer.addEvent(new ChangeUsernameEvent(user.getId(), user.getUsername()));
        return UserMapper.INSTANCE.toDto(user);
    }

    @PutMapping("me/password")
    @Transactional
    public ResponseEntity<EmptyResponse> changePassword(
            @RequestBody ChangePasswordRequest body,
            @RequestAttribute User user
    ) throws ApiErrorException {
        if (!PasswordUtils.validate(body.getNewPassword())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "password is missing or short"));
        }
        if (!PasswordUtils.check(body.getOldPassword(), user.getPassword())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "invalid password"));
        }
        userRepository.updatePassword(PasswordUtils.hash(body.getNewPassword()), user.getId());
        return ResponseEntity.ok(new EmptyResponse());
    }

    @PutMapping("me/color")
    @Transactional
    public UserDto changeColor(@RequestBody UserDto body, @RequestAttribute User user) throws ApiErrorException {
        if (body.getColor() == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "color is required"));
        }
        userRepository.updateColor(user.getId(), body.getColor());
        eventsProducer.addEvent(new ChangeUserColorEvent(user.getId(), body.getColor()));
        return UserMapper.INSTANCE.toDto(userRepository.findUserById(user.getId()));
    }
}
