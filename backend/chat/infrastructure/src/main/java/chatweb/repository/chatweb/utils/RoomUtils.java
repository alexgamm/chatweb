package chatweb.repository.chatweb.utils;

import chatweb.exception.InvalidRoomKeyException;
import chatweb.utils.StringBaseUtils;

public class RoomUtils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static String generateKey(int roomId, String prefix) {
        return String.format("%s-%s", prefix, StringBaseUtils.toBase(roomId, ALPHABET));
    }

    public static int decodeKey(String key) throws InvalidRoomKeyException {
        if (key.contains("-")) {
            String keyWithoutPrefix = key.substring(key.indexOf("-") + 1);
            return StringBaseUtils.fromBase(keyWithoutPrefix, ALPHABET);
        } else {
            throw new InvalidRoomKeyException();
        }
    }
}
