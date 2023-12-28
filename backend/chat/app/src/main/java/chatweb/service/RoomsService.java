package chatweb.service;

import chatweb.entity.Room;
import chatweb.repository.RoomRepository;
import chatweb.repository.UserRepository;
import chatweb.repository.chatweb.utils.RoomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RoomsService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public Room createRoom(int userId, String password, String roomKeyPrefix) {
        String roomKey = String.format("%s-%s", roomKeyPrefix, RoomUtils.generateRoomKey(userId));
        Room room = new Room(
                null,
                roomKey,
                password,
                userRepository.findUserById(userId),
                Collections.emptySet()
        );
        roomRepository.save(room);
        return room;
    }
}
