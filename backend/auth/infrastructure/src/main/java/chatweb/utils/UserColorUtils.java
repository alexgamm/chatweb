package chatweb.utils;

import chatweb.model.Color;

import java.util.Random;

public class UserColorUtils {
    private static final Color[] ALL_COLORS = Color.values();
    private static final Random RANDOM = new Random();

    public static Color getRandomColor() {
        return ALL_COLORS[RANDOM.nextInt(ALL_COLORS.length)];
    }
}
