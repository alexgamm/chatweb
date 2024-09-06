package chatweb.repository;

import chatweb.entity.Room;
import chatweb.model.dto.UserRoomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Room findByKey(String key);

    @Query("SELECT r.id FROM Room r WHERE r.key = :key")
    Integer findRoomIdByKey(String key);

    @Deprecated(since = "use redis", forRemoval = true)
    @Query(value = "SELECT COUNT(ur) > 0 FROM user_rooms ur WHERE ur.room_id = :roomId AND ur.user_id = :userId", nativeQuery = true)
    boolean isUserInRoom(Integer roomId, Integer userId);

    @Query(value = "SELECT user_id, room_id FROM user_rooms", nativeQuery = true)
    Stream<UserRoomDto> getUserRooms();
}