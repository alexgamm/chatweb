package chatweb.service;

import chatweb.model.google.OAuthTokenResponse;
import chatweb.model.google.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class GoogleOAuthService {
    private final static String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private final static String USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final HttpClient httpClient = HttpClients.createDefault();

    public GoogleOAuthService(ObjectMapper objectMapper, String clientId, String clientSecret, String redirectUri) {
        this.objectMapper = objectMapper;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getToken(String code) throws IOException {
        HttpPost request = new HttpPost(TOKEN_URI);
        List<NameValuePair> form = List.of(
                new BasicNameValuePair("client_id", clientId),
                new BasicNameValuePair("client_secret", clientSecret),
                new BasicNameValuePair("code", code),
                new BasicNameValuePair("grant_type", "authorization_code"),
                new BasicNameValuePair("redirect_uri", redirectUri)
        );
        request.setEntity(new UrlEncodedFormEntity(form));
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        OAuthTokenResponse oAuthTokenResponse = objectMapper.readValue(responseBody, OAuthTokenResponse.class);
        return oAuthTokenResponse.getAccessToken();
    }

    public UserInfo getUserInfo(String token) throws IOException {
        HttpGet request;
        try {
            request = new HttpGet(new URIBuilder(USER_INFO_URI).addParameter("access_token", token).build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        return objectMapper.readValue(responseBody, UserInfo.class);
    }
}
