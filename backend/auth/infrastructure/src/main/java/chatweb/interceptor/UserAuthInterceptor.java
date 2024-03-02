package chatweb.interceptor;

import chatweb.helper.UserAuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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
        // сохраняем user в контекст запроса
        request.setAttribute("user", authHelper.auth(request));
        return true;
    }
}
