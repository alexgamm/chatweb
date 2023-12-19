package chatweb.interceptor;

import chatweb.exception.UnauthorizedException;
import chatweb.http.AuthHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@ConditionalOnProperty("service.secret")
@RequiredArgsConstructor
public class ServiceAuthInterceptor implements HandlerInterceptor {
    @Value("${service.secret}")
    private String secret;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        String clientSecret = request.getHeader(AuthHeaders.SERVICE_SECRET);
        if (clientSecret == null || !clientSecret.equals(secret)) {
            throw new UnauthorizedException();
        }
        return true;
    }
}
