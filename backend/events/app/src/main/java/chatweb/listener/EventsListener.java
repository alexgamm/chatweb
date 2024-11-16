package chatweb.listener;

import chatweb.destination.EventsDestination;
import chatweb.model.event.IEvent;
import chatweb.model.event.IRoomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static chatweb.utils.KafkaTopics.EVENTS;

@Component
@RequiredArgsConstructor
public class EventsListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(id = "events", topics = EVENTS)
    public void listen(IEvent event) {
        final String roomKey;
        if (event instanceof IRoomEvent roomEvent) {
            roomKey = roomEvent.getRoom();
        } else {
            roomKey = null;
        }
        messagingTemplate.convertAndSend(EventsDestination.getDestination(roomKey), event);
    }
}
