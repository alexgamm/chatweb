package chatweb.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openai.proxy")
@RequiredArgsConstructor
@Getter
public class OpenAiProxyProperties {
    private final String host;
    private final Integer port;
    private final String name;
    private final String password;
}
