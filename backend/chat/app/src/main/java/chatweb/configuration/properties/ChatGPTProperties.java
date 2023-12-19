package chatweb.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("chatgpt")
public class ChatGPTProperties {
    private String token;
    private String username = "Gosha🤖";
    private String model = "gpt-3.5-turbo";
}
