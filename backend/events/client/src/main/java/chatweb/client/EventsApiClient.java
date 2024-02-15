package chatweb.client;

import chatweb.model.api.OnlineResponse;
import chatweb.model.event.IEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

@Component
public class EventsApiClient {
    private final WebClient webClient;

    public EventsApiClient(@Qualifier("eventsWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Set<Integer> getOnlineUserIds() {
        return webClient.get()
                .uri(uri -> uri.path("/api/events/online").build())
                .retrieve()
                .bodyToMono(OnlineResponse.class)
                .map(OnlineResponse::getOnlineUserIds)
                .block();
    }

    public void addEvent(IEvent event) {
        webClient.post()
                .uri(uri -> uri.path("/api/events").build())
                .bodyValue(event)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
