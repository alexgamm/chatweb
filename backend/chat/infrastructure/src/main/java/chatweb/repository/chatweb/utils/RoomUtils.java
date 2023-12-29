package chatweb.repository.chatweb.utils;

import chatweb.utils.StringBaseUtils;

public class RoomUtils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static String generateKey(int roomId) {
        return StringBaseUtils.toBase(roomId, ALPHABET);
    }

    public static int decodeKey(String key) {
        return StringBaseUtils.fromBase(key, ALPHABET);
    }
}
