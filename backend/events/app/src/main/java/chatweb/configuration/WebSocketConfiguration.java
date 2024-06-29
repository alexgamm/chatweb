package chatweb.configuration;

import chatweb.handler.EventsWebSocketHandler;
import chatweb.helper.UserAuthHelper;
import chatweb.interceptor.UserAuthWebSocketInterceptor;
import chatweb.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final UserAuthHelper userAuthHelper;
    private final EventsService eventsService;


    @Override
    public void registerWebSocketHandlers(@NotNull WebSocketHandlerRegistry registry) {
        registry.addHandler(new EventsWebSocketHandler(eventsService), "/api/ws/events")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new UserAuthWebSocketInterceptor(userAuthHelper));
    }

}
