package chatweb.interceptor;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.helper.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserAuthWebSocketInterceptor implements HandshakeInterceptor {
    private final UserAuthHelper userAuthHelper;

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        User user;
        try {
            String accessToken = Optional.of(UriComponentsBuilder.fromUri(request.getURI()).build())
                    .map(UriComponents::getQueryParams)
                    .map(queryParams -> queryParams.getFirst("access_token"))
                    .filter(token -> !token.isBlank())
                    .orElseThrow(UnauthorizedException::new);
            user = userAuthHelper.auth(accessToken);
        } catch (UnauthorizedException e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        attributes.put("user", user);
        return true;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
