package chatweb.email.context;

public class ResetPasswordContext extends UserContext {

    public ResetPasswordContext(String username, String resetUrl) {
        super(username);
        setVariable("resetUrl", resetUrl);
    }
}
