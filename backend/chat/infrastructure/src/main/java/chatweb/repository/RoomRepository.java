package chatweb.repository;

import chatweb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Room findByRoomKey(String roomKey);

    @Query("SELECT r.id FROM Room r WHERE r.roomKey = :roomKey")
    Integer findRoomIdByRoomKey(String roomKey);

    @Query(value = "SELECT COUNT(ur) > 0 FROM user_rooms ur WHERE ur.room_id = :roomId AND ur.user_id = :userId", nativeQuery = true)
    boolean isUserInRoom(Integer roomId, Integer userId);
}