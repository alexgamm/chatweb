package chatweb.controller.helper;

import chatweb.model.api.LoginResponse;
import chatweb.service.JwtService;

public interface AuthControllerHelper {

    default LoginResponse auth(int userId) {
        String accessToken = getJwtService().createToken(userId);
        return new LoginResponse(accessToken, null);
    }

    JwtService getJwtService();
}
