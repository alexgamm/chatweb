package chatweb.service;

import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.repository.RoomRepository;
import chatweb.utils.PasswordUtils;
import chatweb.utils.RoomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RedisTemplate<String, Integer> redisTemplate;

//    @PostConstruct
//    public void init() {
//        // TODO Clear keys
//        log.info("Start populating user rooms in redis");
//        try (var userRooms = roomRepository.getUserRooms()) {
//            userRooms.forEach(userRoom -> redisTemplate.opsForSet().add(
//                    userRooms(userRoom.userId()),
//                    userRoom.roomId()
//            ));
//        }
//        log.info("Finish populating user rooms in redis");
//    }

    public Room createRoom(int creatorId, String roomPassword, String roomKeyPrefix) {
        User creator = new User(creatorId, null, null, null, null);
        Room room = new Room(
                null,
                null,
                Strings.isBlank(roomPassword) ? null : PasswordUtils.hash(roomPassword),
                creator,
                new HashSet<>()
        );
        room.addUser(creator);
        room = roomRepository.save(room);
        room.setKey(RoomUtils.keyFromId(room.getId(), roomKeyPrefix));
        return roomRepository.save(room);
    }
}
