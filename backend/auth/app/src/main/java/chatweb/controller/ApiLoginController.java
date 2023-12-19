package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginRequest;
import chatweb.model.api.LoginResponse;
import chatweb.repository.UserRepository;
import chatweb.service.JwtService;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/login")
@RequiredArgsConstructor
public class ApiLoginController implements ApiControllerHelper {
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final JwtService jwtService;

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest body) throws ApiErrorException {
        String password = body.getPassword();
        User user = Optional.ofNullable(body.getEmail())
                .map(String::toLowerCase)
                .map(userRepository::findUserByEmail)
                .orElse(null);
        if (user == null || !PasswordUtils.check(password, user.getPassword())) {
            throw new ApiErrorException(new ApiError(HttpStatus.UNAUTHORIZED, "invalid credentials"));
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            verificationService.createAndSendVerification(user);
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            return new LoginResponse(null, true);
        }
        String accessToken = jwtService.createToken(user.getId());
        return new LoginResponse(accessToken, null);
    }
}
