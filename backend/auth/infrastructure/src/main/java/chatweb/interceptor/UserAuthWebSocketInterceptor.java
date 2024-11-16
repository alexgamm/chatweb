package chatweb.interceptor;

import chatweb.helper.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@RequiredArgsConstructor
public class UserAuthWebSocketInterceptor implements ChannelInterceptor {
    private final UserAuthHelper userAuthHelper;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }
        String authHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return message;
        }
        accessor.setUser(userAuthHelper.authByHeader(authHeader)); //TODO handle exception
        return message;
    }
}
