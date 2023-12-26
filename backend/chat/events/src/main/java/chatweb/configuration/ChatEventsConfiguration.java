package chatweb.configuration;

import chatweb.model.event.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatEventsConfiguration {
    @Autowired
    public void configure(ObjectMapper objectMapper) {
        objectMapper.registerSubtypes(
                new NamedType(DeletedMessageEvent.class, DeletedMessageEvent.class.getSimpleName()),
                new NamedType(EditedMessageEvent.class, EditedMessageEvent.class.getSimpleName()),
                new NamedType(NewMessageEvent.class, NewMessageEvent.class.getSimpleName()),
                new NamedType(ReactionEvent.class, ReactionEvent.class.getSimpleName()),
                new NamedType(ServiceReactionEvent.class, ServiceReactionEvent.class.getSimpleName())
        );
    }
}
