package chatweb.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleOAuthWebClientConfiguration {
    @Bean
    @Qualifier("googleOAuthWebClient")
    public WebClient googleOAuthWebClient() {
        return WebClient.create();
    }
}
