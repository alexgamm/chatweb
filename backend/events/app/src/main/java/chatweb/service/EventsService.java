package chatweb.service;

import chatweb.model.event.IEvent;
import chatweb.model.event.IRoomEvent;
import chatweb.model.event.PersonalEventProducer;
import chatweb.model.event.UserOnlineEvent;
import chatweb.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static chatweb.utils.KafkaTopics.Events.ROOT;
import static chatweb.utils.RedisKeys.ONLINE_USER_IDS;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class EventsService {
    private final Map<Integer, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Integer> redisTemplate;

    @PostConstruct
    public void init() {
        redisTemplate.delete(ONLINE_USER_IDS);
    }

    public void addEvent(IEvent event) {
        addEvent((userId) -> event);
    }

    public void addEvent(PersonalEventProducer producer) {
        sessions.forEach((userId, userSessions) -> {
            if (userSessions.isEmpty()) {
                return;
            }
            IEvent event = producer.getPersonalEvent(userId);
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
            redisTemplate.opsForSet().add(ONLINE_USER_IDS, userId);
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
            redisTemplate.opsForSet().remove(ONLINE_USER_IDS, userId);
            addEvent(new UserOnlineEvent(userId, false));
        }
    }

    private int countSessions(int userId) {
        return sessions.getOrDefault(userId, Collections.emptySet()).size();
    }

    private boolean hasSessions(int userId) {
        return countSessions(userId) > 0;
    }

    @KafkaListener(id = "events", topics = ROOT)
    public void listen(IEvent event) {
        if (event instanceof PersonalEventProducer personalEventProducer) {
            addEvent(personalEventProducer);
        } else {
            addEvent(event);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void sendPingMessages() {
        sessions.values().forEach(sessions -> {
            sessions.forEach(session -> {
                try {
                    session.sendMessage(new TextMessage("PING"));
                } catch (Throwable e) {
                    log.warn("Could not send ping", e);
                }
            });
        });
    }
}
