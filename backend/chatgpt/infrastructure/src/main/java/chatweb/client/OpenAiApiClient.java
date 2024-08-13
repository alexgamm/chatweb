package chatweb.client;

import chatweb.exception.chatgpt.ChatCompletionException;
import chatweb.model.openai.ChatCompletionRequest;
import chatweb.model.openai.ChatCompletionResponse;
import chatweb.model.openai.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

@Component
public class OpenAiApiClient {
    private final static String CHAT_COMPLETION_URI = "https://api.openai.com/v1/chat/completions";
    private final WebClient webClient;
    private final String openAiToken;
    private final String openAiModel;

    public OpenAiApiClient(
            @Qualifier("chatGPTWebClient") WebClient webClient,
            @Value("${openai.token}") String openAiToken,
            @Value("${openai.model:gpt-3.5-turbo}") String openAiModel
    ) {
        this.webClient = webClient;
        this.openAiToken = openAiToken;
        this.openAiModel = openAiModel;
    }

    public String getCompletionContent(String message) throws ChatCompletionException {
        ChatCompletionRequest completionRequest = new ChatCompletionRequest(
                openAiModel,
                Collections.singletonList(new Message("user", message))
        );
        ChatCompletionResponse chatCompletionResponse = webClient.post()
                .uri(CHAT_COMPLETION_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(completionRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiToken)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .map(errorBody -> String.format("Unsuccessful response: Status code: %d, Body: %s",
                                        clientResponse.statusCode().value(), errorBody
                                ))
                                .map(ChatCompletionException::new)
                                .flatMap(Mono::error);
                    } else {
                        return clientResponse.bodyToMono(ChatCompletionResponse.class);
                    }
                })
                .block();

        return Optional.ofNullable(chatCompletionResponse)
                .map(ChatCompletionResponse::getChoices)
                .flatMap(list -> list.stream().findFirst())
                .map(ChatCompletionResponse.Choice::getMessage)
                .map(Message::getContent)
                .orElseThrow(() -> new ChatCompletionException("Empty choices were received or response was null"));
    }
}
