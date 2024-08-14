package chatweb.configuration;

import chatweb.model.dto.MessageDto;
import chatweb.model.event.IEvent;
import chatweb.model.event.NewMessageEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Optional;

import static chatweb.utils.KafkaTopics.EVENTS;
import static chatweb.utils.KafkaTopics.OPENAI;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfiguration {
    private String chatGPTName = "Gosha";

    @Bean
    public KStream<String, IEvent> kStream() {
        StreamsBuilder stream = new StreamsBuilder();
        KStream<String, IEvent> eventsStream = stream.stream(EVENTS, Consumed.with(Serdes.String(), new JsonSerde<>(IEvent.class)));

        KStream<String, IEvent> filter = eventsStream.filter((s, event) -> {
                    if (event instanceof NewMessageEvent) {
                        MessageDto message = ((NewMessageEvent) event).getMessage();
                        boolean isChatGPTMentioned = message.getMessage().startsWith(chatGPTName);
                        boolean isReplyToChatGPT = Optional.ofNullable(message.getRepliedMessage())
                                .map(MessageDto::getUserId)
                                .map(id -> id.equals(-1))
                                .orElse(false);
                        return isChatGPTMentioned || isReplyToChatGPT;
                    }
                    return false;
                }
        );

        filter.to(OPENAI, Produced.with(Serdes.String(), new JsonSerde<>(IEvent.class)));

        return eventsStream;
    }

    private static class JsonSerde<T> extends Serdes.WrapperSerde<T> {

        public JsonSerde(Class<T> clazz) {
            super(new JsonSerializer<>(), new JsonDeserializer<>(clazz));
        }
    }
}
