package chatweb.model.auth;

import java.security.Principal;

public record UserPrincipal(int id) implements Principal {
    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
