package chatweb.interceptor;

import chatweb.exception.UnauthorizedException;
import chatweb.helper.UserAuthHelper;
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

    private final UserAuthHelper authHelper;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        String accessToken = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .map(header -> {
                    String[] parts = header.split(" ");
                    if (parts.length != 2) {
                        return null;
                    }
                    return parts[1];
                })
                .filter(token -> !token.isBlank())
                .orElseThrow(UnauthorizedException::new);
        // сохраняем user в контекст запроса
        request.setAttribute("user", authHelper.auth(accessToken));
        return true;
    }
}
