package chatweb.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashUtils {
    private static final HexFormat HEX_FORMAT = HexFormat.of();

    public static byte[] sha256(String payload) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(payload.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    public static String hmacSha256(String payload, byte[] key) {
        return hmac(payload, key, "HmacSHA256");
    }

    public static String hmac(String payload, byte[] key, String algorithm) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return HEX_FORMAT.formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
