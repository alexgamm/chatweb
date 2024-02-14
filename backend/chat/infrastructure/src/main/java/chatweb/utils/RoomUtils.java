package chatweb.utils;

import chatweb.exception.InvalidRoomKeyException;

public class RoomUtils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int KEY_LENGTH = 4;

    public static String keyFromId(int roomId, String prefix) {
        return String.format("%s-%s", prefix, StringBaseUtils.toBase(roomId - 1, ALPHABET, KEY_LENGTH));
    }

    public static int idFromKey(String key) throws InvalidRoomKeyException {
        if (key.contains("-")) {
            String keyWithoutPrefix = key.substring(key.indexOf("-") + 1);
            return StringBaseUtils.fromBase(keyWithoutPrefix, ALPHABET) + 1;
        } else {
            throw new InvalidRoomKeyException();
        }
    }
}
