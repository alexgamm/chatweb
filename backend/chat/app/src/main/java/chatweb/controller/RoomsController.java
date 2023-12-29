package chatweb.controller;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.*;
import chatweb.repository.RoomRepository;
import chatweb.service.RoomsService;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
public class RoomsController {

    private final RoomRepository roomRepository;
    private final RoomsService roomsService;

    @PostMapping
    public CreateRoomResponse createRoom(@RequestAttribute User user, @RequestBody CreateRoomRequest body) {
        Room room = roomsService.createRoom(user, body.getPassword(), body.getPrefix());
        return new CreateRoomResponse(room);
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
        relatedRoom.addUser(user);
        roomRepository.save(relatedRoom);
        return new ApiResponse(true);
    }
}
