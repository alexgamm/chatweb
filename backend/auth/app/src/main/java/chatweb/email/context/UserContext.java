package chatweb.email.context;

import org.thymeleaf.context.AbstractContext;

public abstract class UserContext extends AbstractContext {

    protected UserContext(String username) {
        setVariable("username", username);
    }
}
