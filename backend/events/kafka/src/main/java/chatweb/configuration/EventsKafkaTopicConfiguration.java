package chatweb.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static chatweb.utils.KafkaTopics.Events.ROOT;

@Configuration
public class EventsKafkaTopicConfiguration {
    @Bean
    public NewTopic eventsTopic() {
        return TopicBuilder.name(ROOT).build();
    }
}
