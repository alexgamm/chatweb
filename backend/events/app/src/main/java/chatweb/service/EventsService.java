package chatweb.service;

import chatweb.model.event.Event;
import chatweb.model.event.UserOnlineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class EventsService {
    private final Map<Integer, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEvent(Event event) {
        addEvent((userId) -> event);
    }

    public void addEvent(Function<Integer, Event> eventSupplier) {
        emitters.forEach((userId, userEmitters) -> {
            if (userEmitters.isEmpty()) {
                return;
            }
            Event event = eventSupplier.apply(userId);
            userEmitters.forEach(emitter -> {
                SseEmitter.SseEventBuilder builder = SseEmitter.event()
                        .data(event)
                        .id(UUID.randomUUID().toString())
                        .name(event.getType().name());
                try {
                    emitter.send(builder);
                } catch (Throwable ignored) {
                }
            });
        });
    }

    public SseEmitter createEmitter(int userId) {
        SseEmitter emitter = new SseEmitter();
        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));
        emitters.compute(userId, (key, userEmitters) -> { // лямда должна вернуть новое значение для этого ключа
            if (userEmitters == null) {
                userEmitters = new HashSet<>();
            }
            userEmitters.add(emitter);
            return userEmitters;
        });
        if (countEmitters(userId) == 1) {
            addEvent(new UserOnlineEvent(userId, true));
        }
        return emitter;
    }

    private void removeEmitter(int userId, SseEmitter emitter) {
        emitters.computeIfPresent(userId, (key, userEmitters) -> {
            userEmitters.remove(emitter);
            return userEmitters;
        });
        emitters.values().removeIf(Set::isEmpty);
        if (!hasEmitters(userId)) {
            addEvent(new UserOnlineEvent(userId, false));
        }
    }

    private int countEmitters(int userId) {
        return emitters.getOrDefault(userId, Collections.emptySet()).size();
    }

    private boolean hasEmitters(int userId) {
        return countEmitters(userId) > 0;
    }

    public Set<Integer> getOnlineUserIds() {
        return emitters.keySet();
    }
}
