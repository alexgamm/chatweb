package chatweb.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("chatgpt")
@Getter
@Setter
public class ChatGPTProperties {
    private Integer userId;
    private String mention;
    private String loadingMessage;
    private String errorMessage;
}
