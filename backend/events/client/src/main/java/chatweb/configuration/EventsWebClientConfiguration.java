package chatweb.configuration;

import chatweb.http.AuthHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EventsWebClientConfiguration {
    @Bean
    @Qualifier("eventsWebClient")
    public WebClient eventsWebClient(
            @Value("${events.api.base_url}") String baseUrl,
            @Value("${events.api.secret}") String secret
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(AuthHeaders.SERVICE_SECRET, secret)
                .build();
    }
}
