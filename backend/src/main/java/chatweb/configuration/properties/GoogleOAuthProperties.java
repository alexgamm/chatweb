package chatweb.configuration.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties("google.oauth")
public class GoogleOAuthProperties {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
}
