package chatweb.helper;

import chatweb.exception.UnauthorizedException;
import chatweb.model.auth.UserPrincipal;
import chatweb.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthHelper {

    private final JwtService jwtService;

    @NotNull
    public UserPrincipal authByToken(@NotNull String accessToken) throws UnauthorizedException {
        int userId;
        try {
            userId = jwtService.decodeToken(accessToken);
        } catch (Throwable e) {
            throw new UnauthorizedException();
        }
        return new UserPrincipal(userId);
    }

    @NotNull
    public UserPrincipal authByHeader(@NotNull String header) throws UnauthorizedException {
        String[] parts = header.split(" ");
        if (parts.length != 2) {
            throw new UnauthorizedException();
        }
        String accessToken = parts[1];
        if (accessToken.isBlank()) {
            throw new UnauthorizedException();
        }
        return authByToken(accessToken);
    }
}
