package chatweb.controller;

import chatweb.controller.helper.AuthControllerHelper;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginResponse;
import chatweb.model.google.GoogleOAuthTokenRequest;
import chatweb.model.google.UserInfo;
import chatweb.service.GoogleOAuthService;
import chatweb.service.JwtService;
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
@RequestMapping("api/google/oauth")
@RequiredArgsConstructor
public class ApiGoogleOAuthController implements ApiControllerHelper, AuthControllerHelper {
    private final GoogleOAuthService googleOAuthService;
    private final UserService userService;
    @Getter
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
        User user = userService.findUserByEmail(userInfo.getEmail());
        if (user == null) {
            String username = userInfo.getEmail().split("@")[0];
            user = userService.createUser(username, userInfo.getEmail());
        }
        return auth(user.getId());
    }

}
