package chatweb.configuration.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties("db")
public class DatabaseProperties {
    private final String url;
    private final String user;
    private final String password;
}
