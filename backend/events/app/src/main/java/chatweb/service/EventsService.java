package chatweb.service;

import chatweb.events.EventsServiceGrpc;
import chatweb.events.EventsServiceOuterClass;
import chatweb.model.event.IEvent;
import chatweb.model.event.IRoomEvent;
import chatweb.model.event.PersonalEventProducer;
import chatweb.model.event.UserOnlineEvent;
import chatweb.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class EventsService extends EventsServiceGrpc.EventsServiceImplBase {
    private final Map<Integer, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

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

    @Override
    public void getOnlineUserIds(
            Empty request,
            StreamObserver<EventsServiceOuterClass.UserOnlineResponse> responseObserver
    ) {
        responseObserver.onNext(
                EventsServiceOuterClass.UserOnlineResponse.newBuilder()
                        .addAllOnlineUserIds(sessions.keySet())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void addEvent(EventsServiceOuterClass.AddEventRequest request, StreamObserver<Empty> responseObserver) {
        IEvent event;
        try {
            event = objectMapper.readValue(request.getEventJson(), IEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (event instanceof PersonalEventProducer personalEventProducer) {
            addEvent(personalEventProducer);
        } else {
            addEvent(event);
        }
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
