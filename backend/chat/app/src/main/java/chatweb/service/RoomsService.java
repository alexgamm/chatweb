package chatweb.service;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.repository.RoomRepository;
import chatweb.repository.chatweb.utils.RoomUtils;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RoomsService {

    private final RoomRepository roomRepository;

    public Room createRoom(User user, String roomPassword, String roomKeyPrefix) {
        Room room = new Room(
                null,
                null,
                roomPassword == null ? null : PasswordUtils.hash(roomPassword),
                user,
                Collections.emptySet()
        );
        room = roomRepository.save(room);
        room.setKey(RoomUtils.generateKey(room.getId(), roomKeyPrefix));
        return roomRepository.save(room);
    }
}
