package chatweb.controller;

import chatweb.controller.helper.AuthControllerHelper;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.exception.telegram.TelegramOAuthException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginResponse;
import chatweb.model.telegram.TelegramAuthTokenRequest;
import chatweb.model.telegram.TelegramOAuthResult;
import chatweb.service.JwtService;
import chatweb.service.TelegramOAuthService;
import chatweb.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
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
public class ApiTelegramOAuthController implements ApiControllerHelper, AuthControllerHelper {
    private final TelegramOAuthService telegramOAuthService;
    private final UserService userService;
    @Getter
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
        User user = userService.findUserByEmail(email);
        if (user == null) {
            String username = userInfo.getUsername();
            user = userService.createUser(username, email);
        }
        return auth(user.getId());
    }
}
