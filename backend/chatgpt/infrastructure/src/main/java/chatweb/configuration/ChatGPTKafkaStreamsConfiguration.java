package chatweb.configuration;

import chatweb.model.dto.MessageDto;
import chatweb.model.event.IEvent;
import chatweb.model.event.NewMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Optional;

import static chatweb.utils.KafkaTopics.Events.CHATGPT_TO_PROCESS;
import static chatweb.utils.KafkaTopics.Events.ROOT;

@Configuration
@EnableKafka
@EnableKafkaStreams
@EnableConfigurationProperties(ChatGPTProperties.class)
public class ChatGPTKafkaStreamsConfiguration {

    @Bean
    public JsonSerde<IEvent> eventsSerde(ObjectMapper objectMapper) {
        return new JsonSerde<>(IEvent.class, objectMapper);
    }

    @Bean
    public Predicate<String, IEvent> eventsChatGPTToProcessFilter(ChatGPTProperties chatGPTProperties) {
        return (key, event) -> {
            if (!(event instanceof NewMessageEvent)) {
                return false;
            }
            MessageDto message = ((NewMessageEvent) event).getMessage();
            boolean isChatGPTMentioned = message.getMessage().startsWith(chatGPTProperties.getMention());
            boolean isReplyToChatGPT = Optional.ofNullable(message.getRepliedMessage())
                    .map(MessageDto::getUserId)
                    .map(id -> id.equals(chatGPTProperties.getUserId()))
                    .orElse(false);
            return isChatGPTMentioned || isReplyToChatGPT;
        };
    }

    @Bean
    public KStream<String, IEvent> eventsChatGPTToProcessStream(
            JsonSerde<IEvent> eventsSerde,
            Predicate<String, IEvent> eventsChatGPTToProcessFilter,
            StreamsBuilder kStreamBuilder
    ) {
        KStream<String, IEvent> stream = kStreamBuilder
                .stream(ROOT, Consumed.with(Serdes.String(), eventsSerde))
                .filter(eventsChatGPTToProcessFilter);
        stream.to(CHATGPT_TO_PROCESS, Produced.with(Serdes.String(), eventsSerde));
        return stream;
    }
}
