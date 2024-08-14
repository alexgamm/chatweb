package chatweb.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static chatweb.utils.KafkaTopics.EVENTS;
import static chatweb.utils.KafkaTopics.OPENAI;

@Configuration
public class EventsKafkaTopicConfiguration {
    @Bean
    public NewTopic eventsTopic() {
        return TopicBuilder.name(EVENTS).build();
    }

    @Bean
    public NewTopic openaiTopic() {
        return TopicBuilder.name(OPENAI).build();
    }
}
