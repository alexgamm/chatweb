package chatweb.controller;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.*;
import chatweb.repository.RoomRepository;
import chatweb.repository.UserRepository;
import chatweb.service.RoomsService;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
public class RoomsController implements ApiControllerHelper {

    private final RoomRepository roomRepository;
    private final RoomsService roomsService;
    private final UserRepository userRepository;

    @PostMapping
    public CreateRoomResponse createRoom(@RequestBody CreateRoomRequest body) throws ApiErrorException {
        if (body.getPrefix() == null || body.getPrefix().split(" ").length != 1) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "Missing or invalid prefix"));
        }
        if (!userRepository.existsById(body.getCreatorId())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "There is no such user in the DB"));
        }
        Room room = roomsService.createRoom(body.getCreatorId(), body.getPassword(), body.getPrefix());
        return new CreateRoomResponse(room.getId());
    }

    @PostMapping("{room}/join")
    public ApiResponse joinRoom(
            @PathVariable String room,
            @RequestAttribute User user,
            @RequestBody JoinRoomRequest body
    ) throws ApiErrorException {
        Room relatedRoom = roomRepository.findByKey(room);
        if (relatedRoom == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "Room does not exist"));
        }
        if (relatedRoom.getPassword() != null && !PasswordUtils.check(body.getPassword(), relatedRoom.getPassword())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "incorrect password"));
        }
        if (relatedRoom.getUsers().stream().anyMatch(u -> u.equals(user))) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "You are already in the room"));
        }
        relatedRoom.addUser(user);
        roomRepository.save(relatedRoom);
        return new ApiResponse(true);
    }
}
