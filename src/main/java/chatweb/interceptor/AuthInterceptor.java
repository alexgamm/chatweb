package chatweb.interceptor;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
        if (accessToken != null && !accessToken.isEmpty()) {
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
        } else {
            user = Optional.ofNullable(request.getCookies())
                    .flatMap(cookies -> Arrays.stream(cookies)
                            .filter(cookie -> cookie.getName().equals("sessionId"))
                            .findFirst()
                            .map(Cookie::getValue)
                            // с чем работаем -> что возвращаем
                            .map(sessionId -> sessionRepository.findSessionById(sessionId))
                            .map(session -> userRepository.findUserById(session.getUserId()))
                    )
                    .orElseThrow(() -> new UnauthorizedException());
        }
        // сохраняем user в контекст запроса
        request.setAttribute("user", user);
        return true;
    }
}
