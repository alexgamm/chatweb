package chatweb.email.context;

public class VerificationCodeContext extends UserContext {

    public VerificationCodeContext(String username, String code) {
        super(username);
        setVariable("code", code);
    }
}
