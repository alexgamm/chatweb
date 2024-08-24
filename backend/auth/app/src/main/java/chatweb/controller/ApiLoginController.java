package chatweb.controller;

import chatweb.controller.helper.AuthControllerHelper;
import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginRequest;
import chatweb.model.api.LoginResponse;
import chatweb.service.JwtService;
import chatweb.service.UserService;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/login")
@RequiredArgsConstructor
public class ApiLoginController implements ApiControllerHelper, AuthControllerHelper {
    private final UserService userService;
    private final VerificationService verificationService;
    @Getter
    private final JwtService jwtService;

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest body) throws ApiErrorException {
        String password = body.getPassword();
        User user = userService.findUserByEmail(body.getEmail());
        if (user == null || !PasswordUtils.check(password, user.getPassword())) {
            throw ApiError.badRequest("invalid credentials").toException();
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            verificationService.createAndSendVerification(user);
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            return new LoginResponse(null, true);
        }
        return auth(user.getId());
    }
}
