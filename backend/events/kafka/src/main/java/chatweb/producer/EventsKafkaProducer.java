package chatweb.producer;

import chatweb.model.event.IEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static chatweb.utils.KafkaTopics.EVENTS;

@Component
@RequiredArgsConstructor
public class EventsKafkaProducer {
    private final KafkaTemplate<String, IEvent> kafkaTemplate;

    public void addEvent(IEvent event) {
        kafkaTemplate.send(EVENTS, event);
    }
}
