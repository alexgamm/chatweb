package chatweb.helper;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.repository.UserRepository;
import chatweb.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthHelper {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @NotNull
    public User auth(@NotNull String accessToken) throws UnauthorizedException {
        int userId;
        try {
            userId = jwtService.decodeToken(accessToken);
        } catch (Throwable e) {
            throw new UnauthorizedException();
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UnauthorizedException();
        }
        return user;
    }
}
