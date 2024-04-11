package chatweb.controller;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiResponse;
import chatweb.model.api.CreateRoomRequest;
import chatweb.model.api.CreateRoomResponse;
import chatweb.model.api.JoinRoomRequest;
import chatweb.repository.RoomRepository;
import chatweb.repository.UserRepository;
import chatweb.service.RoomsService;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static chatweb.model.api.ApiError.badRequest;

@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
public class RoomsController implements ApiControllerHelper {

    private final RoomRepository roomRepository;
    private final RoomsService roomsService;
    private final UserRepository userRepository;

    @PostMapping
    public CreateRoomResponse createRoom(@RequestBody CreateRoomRequest body) throws ApiErrorException {
        if (!userRepository.existsById(body.getCreatorId())) {
            throw badRequest("Creator not found").toException();
        }
        Room room = roomsService.createRoom(body.getCreatorId(), body.getPassword(), body.getPrefix());
        return new CreateRoomResponse(room.getId(), room.getKey());
    }

    @PostMapping("{room}/join")
    public ApiResponse joinRoom(
            @PathVariable String room,
            @RequestAttribute User user,
            @RequestBody JoinRoomRequest body
    ) throws ApiErrorException {
        Room relatedRoom = roomRepository.findByKey(room);
        if (relatedRoom == null) {
            throw badRequest("Room does not exist").toException();
        }
        if (relatedRoom.getPassword() != null && !PasswordUtils.check(body.getPassword(), relatedRoom.getPassword())) {
            throw badRequest("Incorrect password").toException();
        }
        if (relatedRoom.getUsers().contains(user)) {
            return new ApiResponse(true);
        }
        relatedRoom.addUser(user);
        roomRepository.save(relatedRoom);
        return new ApiResponse(true);
    }

}
