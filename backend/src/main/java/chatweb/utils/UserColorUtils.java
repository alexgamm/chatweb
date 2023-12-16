package chatweb.utils;

import chatweb.model.user.UserColor;

import java.util.Random;

public class UserColorUtils {
    private static final UserColor[] ALL_COLORS = UserColor.values();
    private static final Random RANDOM = new Random();

    public static UserColor getRandomColor() {
        return ALL_COLORS[RANDOM.nextInt(ALL_COLORS.length)];
    }
}
