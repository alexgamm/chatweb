package chatweb.configuration.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties("smtp")
public class SmtpProperties {
    private final String host;
    private final int port;
    private final String email;
    private final String fromName;
    private final String password;
}
