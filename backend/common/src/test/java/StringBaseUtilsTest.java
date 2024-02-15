import chatweb.utils.StringBaseUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringBaseUtilsTest {
    @Test
    public void testToBase() {
        String result = StringBaseUtils.toBase(1, "abcdefghijklmnopqrstuvwxyz");
        assertEquals("b", result, "Should return 'b'");
    }
    @Test
    public void testToBaseWithLength() {
        String result = StringBaseUtils.toBase(1, "abcdefghijklmnopqrstuvwxyz", 4);
        assertEquals("aaab", result, "Should return 'aaab'");
    }

    @Test
    public void testFromBase() {
        int result = StringBaseUtils.fromBase("aaab", "abcdefghijklmnopqrstuvwxyz");
        assertEquals(1, result, "Should return '1'");
    }
}
