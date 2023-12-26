package chatweb.controller;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.*;
import chatweb.repository.RoomRepository;
import chatweb.service.RoomsService;
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
        Room room = roomsService.createRoom(user.getId(), body.getPassword());
        return new CreateRoomResponse(room.getRoomKey());
    }

    @PatchMapping("{roomKey}/join")
    public ApiResponse joinRoom(
            @PathVariable String roomKey,
            @RequestAttribute User user,
            @RequestBody JoinRoomRequest body
    ) throws ApiErrorException {
        Room room = roomRepository.findByRoomKey(roomKey);
        if (room == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "Room does not exist"));
        }
        if (room.getPassword() != null && !body.getPassword().equals(room.getPassword())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "incorrect password"));
        }
        roomRepository.addUserToRoom(room, user);
        return new ApiResponse(true);
    }
}
