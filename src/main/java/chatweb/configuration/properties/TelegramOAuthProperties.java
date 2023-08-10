package chatweb.configuration.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties("tg.oauth")
public class TelegramOAuthProperties {
    private final String publicKey;
    private final String botId;
    private final String botToken;
    private final String redirectUri;
}
