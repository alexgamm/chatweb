package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.GoogleOAuthTokenRequest;
import chatweb.model.api.LoginResponse;
import chatweb.model.google.UserInfo;
import chatweb.repository.UserRepository;
import chatweb.service.GoogleOAuthService;
import chatweb.service.JwtService;
import chatweb.service.VerificationService;
import chatweb.utils.UserColorUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("api/google/oauth")
@RequiredArgsConstructor
public class ApiGoogleOAuthController implements ApiControllerHelper {
    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final JwtService jwtService;

    @GetMapping
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleOAuthService.getOauthUrl());
    }

    @PostMapping("token")
    public LoginResponse token(@RequestBody GoogleOAuthTokenRequest body) throws ApiErrorException {
        UserInfo userInfo;
        try {
            String token = googleOAuthService.getToken(body.getCode());
            userInfo = googleOAuthService.getUserInfo(token);
        } catch (IOException e) {
            throw new ApiErrorException(new ApiError(HttpStatus.SERVICE_UNAVAILABLE, "external error"));
        }
        User user = userRepository.findUserByEmail(userInfo.getEmail().toLowerCase());
        if (user == null) {
            String username = userInfo.getEmail().split("@")[0];
            user = new User(
                    null,
                    username,
                    userInfo.getEmail().toLowerCase(),
                    null,
                    new Date(),
                    UserColorUtils.getRandomColor()
            );
            user = userRepository.save(user);
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            //TODO return newly created verification
            verificationService.createVerification(user.getId(), "");
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        String accessToken = jwtService.createToken(user.getId());
        return new LoginResponse(accessToken, null);
    }
}
