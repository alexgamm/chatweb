package chatweb.service;

import chatweb.configuration.properties.GoogleOAuthProperties;
import chatweb.model.google.OAuthTokenResponse;
import chatweb.model.google.UserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
@EnableConfigurationProperties(GoogleOAuthProperties.class)
public class GoogleOAuthService {
    private final static String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private final static String USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final static String OAUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private final GoogleOAuthProperties googleOAuthProperties;
    private final WebClient webClient;

    public GoogleOAuthService(
            GoogleOAuthProperties googleOAuthProperties,
            @Qualifier("googleOAuthWebClient") WebClient webClient
    ) {
        this.googleOAuthProperties = googleOAuthProperties;
        this.webClient = webClient;
    }

    public String getToken(String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", googleOAuthProperties.getClientId());
        formData.add("client_secret", googleOAuthProperties.getClientSecret());
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", googleOAuthProperties.getRedirectUri());

        return webClient.post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuthTokenResponse.class)
                .map(OAuthTokenResponse::getAccessToken)
                .block();
    }

    public UserInfo getUserInfo(String token) throws IOException {
        return webClient.get()
                .uri(USER_INFO_URI, uriBuilder -> uriBuilder
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }

    public String getOauthUrl() {
        return UriComponentsBuilder.fromHttpUrl(OAUTH_URI)
                .queryParam("client_id", googleOAuthProperties.getClientId())
                .queryParam("redirect_uri", googleOAuthProperties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "email")
                .build()
                .toString();
    }
}
