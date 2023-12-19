package chatweb.model.api;

import lombok.Getter;

@Getter
public class RegistrationRequest {
    private String email;
    private String username;
    private String password;
}
