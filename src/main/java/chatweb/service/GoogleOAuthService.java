package chatweb.service;

import chatweb.configuration.properties.GoogleOAuthProperties;
import chatweb.model.google.OAuthTokenResponse;
import chatweb.model.google.UserInfo;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
@EnableConfigurationProperties(GoogleOAuthProperties.class)
public class GoogleOAuthService {
    private final static String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private final static String USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final static String OAUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private final GoogleOAuthProperties googleOAuthProperties;
    private final WebClient webClient = WebClient.create();


    public GoogleOAuthService(GoogleOAuthProperties googleOAuthProperties) {
        this.googleOAuthProperties = googleOAuthProperties;
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
        WebClient webClient = WebClient.create(USER_INFO_URI);
        Mono<UserInfo> userInfo = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(UserInfo.class);
        return userInfo.block();
    }

    public String getOauthUrl() {
        try {
            return new URIBuilder(OAUTH_URI)
                    .addParameter("client_id", googleOAuthProperties.getClientId())
                    .addParameter("redirect_uri", googleOAuthProperties.getRedirectUri())
                    .addParameter("response_type", "code")
                    .addParameter("scope", "email")
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
