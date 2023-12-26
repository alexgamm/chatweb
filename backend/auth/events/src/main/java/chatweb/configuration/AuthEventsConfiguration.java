package chatweb.configuration;

import chatweb.model.event.ChangeUserColorEvent;
import chatweb.model.event.ChangeUsernameEvent;
import chatweb.model.event.UserOnlineEvent;
import chatweb.model.event.UserTypingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthEventsConfiguration {
    @Autowired
    public void configure(ObjectMapper objectMapper) {
        objectMapper.registerSubtypes(
                new NamedType(ChangeUserColorEvent.class, ChangeUserColorEvent.class.getSimpleName()),
                new NamedType(ChangeUsernameEvent.class, ChangeUsernameEvent.class.getSimpleName()),
                new NamedType(UserOnlineEvent.class, UserOnlineEvent.class.getSimpleName()),
                new NamedType(UserTypingEvent.class, UserTypingEvent.class.getSimpleName())
        );
    }
}
