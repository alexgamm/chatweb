package chatweb.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Configuration
@EnableConfigurationProperties(OpenAiProxyProperties.class)
@RequiredArgsConstructor
public class OpenAiWebClientConfiguration {
    private final OpenAiProxyProperties properties;

    @Bean
    @Qualifier("openAiWebClient")
    public WebClient openAiWebClient() {
        // TODO remove port from this part of code
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host(properties.getHost())
                        .port(properties.getPort())
                        .username(properties.getName())
                        .password(password -> properties.getPassword())
                );
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
