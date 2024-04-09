package chatweb.handler;

import chatweb.entity.User;
import chatweb.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class EventsWebSocketHandler extends TextWebSocketHandler {
    private final EventsService eventsService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        eventsService.addSession(getUser(session).getId(), session);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        eventsService.removeSession(getUser(session).getId(), session);
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) {
        eventsService.removeSession(getUser(session).getId(), session);
    }

    @NotNull
    private static User getUser(WebSocketSession session) {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            throw new IllegalStateException("Attribute user not found");
        }
        return user;
    }

}
