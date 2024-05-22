package chatweb.client;

import chatweb.model.api.CreateRoomRequest;
import chatweb.model.api.CreateRoomResponse;
import chatweb.model.api.MessageIdResponse;
import chatweb.model.api.service.SendServiceMessageRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ChatApiClient {
    private final WebClient webClient;

    public ChatApiClient(@Qualifier("chatWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public CreateRoomResponse createRoom(CreateRoomRequest body) {
        return webClient.post()
                .uri(uri -> uri.path("/api/rooms").build())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CreateRoomResponse.class)
                .block();
    }

    public MessageIdResponse sendMessage(SendServiceMessageRequest body) {
        return webClient.post()
                .uri(uri -> uri.path("/api/service/messages").build())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(MessageIdResponse.class)
                .block();
    }
}
