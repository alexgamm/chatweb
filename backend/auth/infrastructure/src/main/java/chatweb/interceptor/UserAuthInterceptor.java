package chatweb.interceptor;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.helper.UserAuthHelper;
import chatweb.model.auth.UserPrincipal;
import chatweb.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserAuthInterceptor implements HandlerInterceptor {

    private final UserAuthHelper authHelper;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        UserPrincipal principal = authHelper.authByHeader(request.getHeader(HttpHeaders.AUTHORIZATION));
        User user = userRepository.findUserById(principal.id());
        if (user == null) {
            throw new UnauthorizedException();
        }
        // сохраняем user в контекст запроса
        request.setAttribute("user", user); // TODO split findUser
        return true;
    }
}
