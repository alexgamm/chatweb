package chatweb.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static chatweb.utils.KafkaTopics.EVENTS;

@Configuration
public class EventsKafkaTopicConfiguration {
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(EVENTS).build();
    }
}
