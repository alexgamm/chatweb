package chatweb.utils;

public class StringBaseUtils {

    public static String toBase(int source, String alphabet) {
        StringBuilder result = new StringBuilder();
        while (source > alphabet.length() - 1) {
            int remainder = source % alphabet.length();
            source = source / alphabet.length();
            result.insert(0, alphabet.charAt(remainder));
        }
        result.insert(0, alphabet.charAt(source));
        return result.toString();
    }

    public static int fromBase(String source, String alphabet) {
        int result = 0;
        for (int i = source.length() - 1; i >= 0; i--) {
            int letterIdx = alphabet.indexOf(source.charAt(i));
            double multiplier = Math.pow(alphabet.length(), source.length() - 1 - i);
            result += (int) (letterIdx * multiplier);
        }
        return result;
    }

}
