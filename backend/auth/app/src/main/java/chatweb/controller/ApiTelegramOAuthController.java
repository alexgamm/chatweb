package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.exception.telegram.TelegramOAuthException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginResponse;
import chatweb.model.telegram.TelegramAuthTokenRequest;
import chatweb.model.telegram.TelegramOAuthResult;
import chatweb.repository.UserRepository;
import chatweb.service.JwtService;
import chatweb.service.TelegramOAuthService;
import chatweb.service.VerificationService;
import chatweb.utils.UserColorUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/tg/oauth")
@RequiredArgsConstructor
public class ApiTelegramOAuthController implements ApiControllerHelper {
    private final TelegramOAuthService telegramOAuthService;
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final JwtService jwtService;

    @GetMapping
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(telegramOAuthService.getOauthUrl());
    }

    @PostMapping("token")
    public LoginResponse token(@RequestBody TelegramAuthTokenRequest body) throws ApiErrorException {
        TelegramOAuthResult userInfo;
        try {
            userInfo = telegramOAuthService.parseOAuthResult(body.getAuthResult());
        } catch (TelegramOAuthException e) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "auth error"));
        }
        String email = String.format("%s@t.me", userInfo.getId());
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            String username = userInfo.getUsername();
            user = new User(
                    null,
                    username,
                    email,
                    null,
                    UserColorUtils.getRandomColor()
            );
            user = userRepository.save(user);
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            verification = verificationService.createVerification(user.getId(), "");
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        String accessToken = jwtService.createToken(user.getId());
        return new LoginResponse(accessToken, null);
    }
}
