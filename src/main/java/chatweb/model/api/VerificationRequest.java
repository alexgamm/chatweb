package chatweb.model.api;

import lombok.Getter;

@Getter
public class VerificationRequest {
    private String email;
    private String code;
}
