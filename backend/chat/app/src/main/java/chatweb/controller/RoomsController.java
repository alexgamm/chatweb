package chatweb.controller;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiResponse;
import chatweb.model.api.JoinRoomRequest;
import chatweb.repository.RoomRepository;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static chatweb.model.api.ApiError.badRequest;

@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
public class RoomsController implements ApiControllerHelper {

    private final RoomRepository roomRepository;

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
        if (relatedRoom.getUsers().contains(user)) {
            return new ApiResponse(true);
        }
        if (relatedRoom.getPassword() != null && !PasswordUtils.check(body.getPassword(), relatedRoom.getPassword())) {
            throw badRequest("Incorrect password").toException();
        }
        relatedRoom.addUser(user);
        roomRepository.save(relatedRoom);
        return new ApiResponse(true);
    }

}
