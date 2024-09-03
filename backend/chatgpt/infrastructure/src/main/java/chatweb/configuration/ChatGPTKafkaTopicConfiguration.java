package chatweb.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static chatweb.utils.KafkaTopics.Events.CHATGPT_TO_PROCESS;

@Configuration
public class ChatGPTKafkaTopicConfiguration {
    @Bean
    public NewTopic eventsChatGPTToProcessTopic() {
        return TopicBuilder.name(CHATGPT_TO_PROCESS).build();
    }
}
