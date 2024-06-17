package chatweb.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class KafkaConfiguration {
    @Bean
    public JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new JsonMessageConverter(objectMapper);
    }
}
