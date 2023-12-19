package chatweb.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ChatGPTWebClientConfiguration {
    @Bean
    @Qualifier("chatGPTWebClient")
    public WebClient chatGPTWebClient() {
        return WebClient.create();
    }
}
