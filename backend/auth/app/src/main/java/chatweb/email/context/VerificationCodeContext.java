package chatweb.email.context;

import org.thymeleaf.context.AbstractContext;

public class VerificationCodeContext extends AbstractContext {

    public VerificationCodeContext(String code) {
        setVariable("code", code);
    }
}
