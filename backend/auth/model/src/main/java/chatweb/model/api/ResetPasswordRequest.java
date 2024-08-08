package chatweb.model.api;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String sessionId;
    private String newPassword;
}
