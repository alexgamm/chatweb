package chatweb.repository;

import chatweb.entity.Room;
import chatweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Room findByRoomKey(String roomKey);

    @Query("SELECT COUNT(ur) > 0 FROM user_rooms ur WHERE ur.room_id = :roomId AND ur.user_id = :userId")
    boolean isUserInRoom(Integer roomId, Integer userId);

    default void addUserToRoom(Room room, User user) {
        room.getUsers().add(user);
        this.save(room);
    }


}