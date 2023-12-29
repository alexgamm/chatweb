package chatweb.controller;

import chatweb.client.EventsApiClient;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.exception.InvalidRoomKeyException;
import chatweb.mapper.UserMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.ChangePasswordRequest;
import chatweb.model.api.EmptyResponse;
import chatweb.model.api.UserListResponse;
import chatweb.model.dto.UserDto;
import chatweb.model.event.ChangeUserColorEvent;
import chatweb.model.event.ChangeUsernameEvent;
import chatweb.model.event.UserTypingEvent;
import chatweb.repository.RoomRepository;
import chatweb.repository.UserRepository;
import chatweb.repository.chatweb.utils.RoomUtils;
import chatweb.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsersController implements ApiControllerHelper {
    private final UserRepository userRepository;
    private final EventsApiClient eventsApi;
    private final RoomRepository roomRepository;

    @GetMapping
    public UserListResponse users() {
        // TODO introduce pagination
        Set<Integer> onlineUserIds = eventsApi.getOnlineUserIds();
        List<UserListResponse.User> list = userRepository.findAll().stream()
                .map(user -> new UserListResponse.User(
                        user.getId(),
                        user.getUsername(),
                        user.getLastActivityAt(),
                        onlineUserIds.contains(user.getId()),
                        user.getColor()
                ))
                .toList();
        return new UserListResponse(list);
    }

    @GetMapping("me")
    public UserDto getMe(@RequestAttribute User user) {
        return UserMapper.userToUserDto(user);
    }

    @PutMapping("me/typing")
    public ResponseEntity<EmptyResponse> typing(
            @RequestAttribute User user,
            @RequestParam(required = false) String room
    ) throws ApiErrorException {
        Integer roomId;
        if (room != null) {
            try {
                roomId = RoomUtils.decodeKey(room);
            } catch (InvalidRoomKeyException e) {
                throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "Invalid key format"));
            }
        } else {
            roomId = null;
        }
        if (roomId != null && !roomRepository.isUserInRoom(roomId, user.getId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "you are not in the room"));
        }
        eventsApi.addEvent(new UserTypingEvent(user.getId(), roomId));
        return ResponseEntity.ok(new EmptyResponse());
    }

    @PutMapping("me/username")
    @Transactional
    public UserDto changeUsername(@RequestBody UserDto input, @RequestAttribute User user) throws ApiErrorException {
        if (input.getUsername().equals("")) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "invalid username"));
        }
        if (userRepository.findUserByUsername(input.getUsername()) != null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "username is already taken"));
        }
        userRepository.updateUsername(user.getId(), input.getUsername());
        user = userRepository.findUserById(user.getId());
        eventsApi.addEvent(new ChangeUsernameEvent(user.getId(), user.getUsername()));
        return UserMapper.userToUserDto(user);
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
        eventsApi.addEvent(new ChangeUserColorEvent(user.getId(), body.getColor()));
        return UserMapper.userToUserDto(userRepository.findUserById(user.getId()));
    }
}
