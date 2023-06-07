package chatweb.entity;

public class Verification {
    private final int userId;
    private final String code;
    private final boolean verified;

    public Verification(int userId, String code, boolean verified) {
        this.userId = userId;
        this.code = code;
        this.verified = verified;
    }

    public int getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public boolean isVerified() {
        return verified;
    }
}
