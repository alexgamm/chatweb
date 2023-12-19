package chatweb.interceptor;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.repository.UserRepository;
import chatweb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthInterceptor implements HandlerInterceptor {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        User user;
        String accessToken = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .map(header -> {
                    String[] parts = header.split(" ");
                    if (parts.length != 2) {
                        return null;
                    }
                    return parts[1];
                })
                .filter(token -> !token.isBlank())
                .orElse(request.getParameter("access_token"));
        if (accessToken == null || accessToken.isEmpty()) {
            throw new UnauthorizedException();
        }
        int userId;
        try {
            userId = jwtService.decodeToken(accessToken);
        } catch (Throwable e) {
            throw new UnauthorizedException();
        }
        user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UnauthorizedException();
        }
        // сохраняем user в контекст запроса
        request.setAttribute("user", user);
        return true;
    }
}
