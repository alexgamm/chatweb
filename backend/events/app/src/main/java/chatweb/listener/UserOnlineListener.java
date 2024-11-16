package chatweb.listener;

import chatweb.model.auth.UserPrincipal;
import chatweb.model.event.UserOnlineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static chatweb.destination.EventsDestination.getDestination;
import static chatweb.utils.RedisKeys.ONLINE_USER_IDS;

@Component
@RequiredArgsConstructor
public class UserOnlineListener {

    private final SimpUserRegistry userRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Integer> redisTemplate;


    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        var user = ((UserPrincipal) accessor.getUser());
        if (user == null) {
            return;
        }
        redisTemplate.opsForSet().add(ONLINE_USER_IDS, user.id());
        messagingTemplate.convertAndSend(getDestination(null), new UserOnlineEvent(user.id(), true));
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        var user = ((UserPrincipal) accessor.getUser());
        if (user == null) {
            return;
        }
        String username = user.getName();
        if (userRegistry.getUser(username) != null) {
            return;
        }
        redisTemplate.opsForSet().remove(ONLINE_USER_IDS, user.id());
        messagingTemplate.convertAndSend(getDestination(null), new UserOnlineEvent(user.id(), false));
    }

}
