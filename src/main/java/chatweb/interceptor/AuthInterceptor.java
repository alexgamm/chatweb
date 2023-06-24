package chatweb.interceptor;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals("sessionId"))
                        .findFirst()
                        .map(Cookie::getValue)
                        // с чем работаем -> что возвращаем
                        .map(sessionId -> sessionRepository.findSessionById(sessionId))
                        .map(session -> userRepository.findUserById(session.getUserId()))
                )
                .orElseThrow(() -> new UnauthorizedException());
        // сохраняем user в контекст запроса
        request.setAttribute("user", user);
        return true;
    }
}
