package chatweb.utils;

public class RedisKeys {
    public static final String ONLINE_USER_IDS = "online_user_ids";

    public static String userRooms(int userId) {
        return String.format("user_rooms_%d", userId);
    }
}
