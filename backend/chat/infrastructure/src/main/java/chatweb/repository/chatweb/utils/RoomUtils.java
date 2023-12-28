package chatweb.repository.chatweb.utils;

import chatweb.utils.StringBaseUtils;

public class RoomUtils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static String generateRoomKey(int userId) {
        return StringBaseUtils.toBase(userId, ALPHABET);
    }

    public static int decodeRoomKey(String roomKey) {
        return StringBaseUtils.fromBase(roomKey, ALPHABET);
    }
}
