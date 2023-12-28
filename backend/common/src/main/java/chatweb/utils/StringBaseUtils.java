package chatweb.utils;

public class StringBaseUtils {

    public static String toBase(int userId, String alphabet) {
        String roomKey = "";
        while (userId > alphabet.length() - 1) {
            int remainder = userId % alphabet.length();
            userId = userId / alphabet.length();
            roomKey = alphabet.charAt(remainder) + roomKey;
        }
        roomKey = alphabet.charAt(userId) + roomKey;
        return roomKey;
    }

    public static int fromBase(String roomKey, String alphabet) {
        int userId = 0;
        for (int i = roomKey.length() - 1; i >= 0; i--) {
            userId += (int) (alphabet.indexOf(
                    roomKey.charAt(i)) * Math.pow(alphabet.length(),
                    roomKey.length() - 1 - i)
            );
        }
        return userId;
    }

}
