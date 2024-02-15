package chatweb.service;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.repository.RoomRepository;
import chatweb.utils.PasswordUtils;
import chatweb.utils.RoomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class RoomsService {

    private final RoomRepository roomRepository;

    public Room createRoom(int creatorId, String roomPassword, String roomKeyPrefix) {
        User creator = new User(creatorId, null, null, null, null);
        Room room = new Room(
                null,
                null,
                roomPassword == null ? null : PasswordUtils.hash(roomPassword),
                creator,
                new HashSet<>()
        );
        room.addUser(creator);
        room = roomRepository.save(room);
        room.setKey(RoomUtils.keyFromId(room.getId(), roomKeyPrefix));
        return roomRepository.save(room);
    }
}
