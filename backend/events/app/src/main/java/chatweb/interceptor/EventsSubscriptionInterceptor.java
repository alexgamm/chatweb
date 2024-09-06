package chatweb.interceptor;

import chatweb.destination.EventsDestination;
import chatweb.exception.UnauthorizedException;
import chatweb.model.auth.UserPrincipal;
import chatweb.utils.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@RequiredArgsConstructor
public class EventsSubscriptionInterceptor implements ChannelInterceptor {
    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }
        EventsDestination destination = EventsDestination.fromString(accessor.getDestination()); //TODO handle exception
        if (destination.getRoomId() == null) {
            return message;
        }
        UserPrincipal user = (UserPrincipal) accessor.getUser();
        if (user == null) {
            throw new UnauthorizedException();
        }
        var isUserInRoom = redisTemplate.opsForSet().isMember(RedisKeys.userRooms(user.id()), destination.getRoomId());
        if (!Boolean.TRUE.equals(isUserInRoom)) {
            throw new UnauthorizedException();
        }
        return message;
    }
}
