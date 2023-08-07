package chatweb.utils;

import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

public class PasswordUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX_FORMAT = HexFormat.of();

    private static byte[] sha512(byte[] salt, String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(salt);
        md.update(password.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] saltAndPassword = sha512(salt, password);
        String saltAndPasswordHex = HEX_FORMAT.formatHex(saltAndPassword);
        String saltHex = HEX_FORMAT.formatHex(salt);
        return saltHex + ":" + saltAndPasswordHex;
    }

    public static boolean check(String password, String hashedPassword) {
        if (password == null || password.isBlank() || hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        String[] split = hashedPassword.split(":");
        if (split.length != 2) {
            return false;
        }
        String saltHex = split[0];
        String saltAndPasswordHex = split[1];
        byte[] saltAndPassword = sha512(HEX_FORMAT.parseHex(saltHex), password);
        return HEX_FORMAT.formatHex(saltAndPassword).equals(saltAndPasswordHex);
    }

    public static boolean validate(String password) {
        return password != null && !password.isBlank() && password.length() > 6;
    }
}
