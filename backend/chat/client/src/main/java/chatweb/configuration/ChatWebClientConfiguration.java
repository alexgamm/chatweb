package chatweb.configuration;

import chatweb.http.AuthHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ChatWebClientConfiguration {
    @Bean
    @Qualifier("chatWebClient")
    public WebClient chatWebClient(
            @Value("${chat.api.base.url}") String chatUrl,
            @Value("${chat.api.secret}") String chatSecret
    ) {
        return WebClient.builder()
                .baseUrl(chatUrl)
                .defaultHeader(AuthHeaders.SERVICE_SECRET, chatSecret)
                .build();
    }
}
