package chatweb.service;

import chatweb.model.event.IEvent;
import chatweb.model.event.IRoomEvent;
import chatweb.model.event.UserOnlineEvent;
import chatweb.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventsService {
    private final Map<Integer, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    public void addEvent(IEvent event) {
        addEvent((userId) -> event);
    }

    public void addEvent(Function<Integer, IEvent> eventSupplier) {
        sessions.forEach((userId, userSessions) -> {
            if (userSessions.isEmpty()) {
                return;
            }
            IEvent event = eventSupplier.apply(userId);
            if (event instanceof IRoomEvent roomEvent && roomEvent.getRoomId() != null) {
                if (!roomRepository.isUserInRoom(roomEvent.getRoomId(), userId)) {
                    return;
                }
            }
            String eventJson;
            try {
                eventJson = objectMapper.writeValueAsString(event);
            } catch (JsonProcessingException ex) {
                log.warn("Could not convert event {} to json", event, ex);
                return;
            }
            userSessions.forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(eventJson));
                } catch (Throwable ignored) {
                }
            });
        });
    }

    public void addSession(int userId, WebSocketSession session) {
        sessions.compute(userId, (key, userSessions) -> { // лямда должна вернуть новое значение для этого ключа
            if (userSessions == null) {
                userSessions = new HashSet<>();
            }
            userSessions.add(session);
            return userSessions;
        });
        if (countSessions(userId) == 1) {
            addEvent(new UserOnlineEvent(userId, true));
        }
    }

    public void removeSession(int userId, WebSocketSession session) {
        sessions.computeIfPresent(userId, (key, userSessions) -> {
            userSessions.remove(session);
            return userSessions;
        });
        sessions.values().removeIf(Set::isEmpty);
        if (!hasSessions(userId)) {
            addEvent(new UserOnlineEvent(userId, false));
        }
    }

    private int countSessions(int userId) {
        return sessions.getOrDefault(userId, Collections.emptySet()).size();
    }

    private boolean hasSessions(int userId) {
        return countSessions(userId) > 0;
    }

    public Set<Integer> getOnlineUserIds() {
        return sessions.keySet();
    }
}
