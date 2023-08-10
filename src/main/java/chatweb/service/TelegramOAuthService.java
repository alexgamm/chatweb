package chatweb.service;

import chatweb.configuration.properties.GoogleOAuthProperties;
import chatweb.configuration.properties.TelegramOAuthProperties;
import chatweb.model.google.OAuthTokenResponse;
import chatweb.model.google.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
@EnableConfigurationProperties(TelegramOAuthProperties.class)
@RequiredArgsConstructor
public class TelegramOAuthService {
    private final static String OAUTH_URI = "https://oauth.telegram.org/auth";
    private final ObjectMapper objectMapper;
    private final TelegramOAuthProperties telegramOAuthProperties;

    public UserInfo getUserInfo(String authResult) throws IOException {
        return null;
    }

    public String getOauthUrl() {
        try {
            return new URIBuilder(OAUTH_URI)
                    .addParameter("bot_id", telegramOAuthProperties.getBotId())
                    .addParameter("public_key", telegramOAuthProperties.getPublicKey())
                    .addParameter("origin", telegramOAuthProperties.getRedirectUri())
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
