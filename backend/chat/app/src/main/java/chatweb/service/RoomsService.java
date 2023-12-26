package chatweb.service;

import chatweb.entity.Room;
import chatweb.repository.RoomRepository;
import chatweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@RequiredArgsConstructor
public class RoomsService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String ROOM_KEY_PREFIX = "codenames-";
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public Room createRoom(int userId, String password) {
        String roomKey = ROOM_KEY_PREFIX + generateRoomKey(userId);
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

    public static String generateRoomKey(int userId) {
        String roomKey = "";
        while (userId > ALPHABET.length() - 1) {
            int remainder = userId % ALPHABET.length();
            userId = userId / ALPHABET.length();
            roomKey = ALPHABET.charAt(remainder) + roomKey;
        }
        roomKey = ALPHABET.charAt(userId) + roomKey;
        return roomKey;
    }

    public static int decodeRoomKey(String roomKey) {
        int userId = 0;
        for (int i = roomKey.length() - 1; i >= 0; i--) {
            userId += (int) (ALPHABET.indexOf(
                    roomKey.charAt(i)) * Math.pow(ALPHABET.length(),
                    roomKey.length() - 1 - i)
            );
        }
        return userId;
    }
}
