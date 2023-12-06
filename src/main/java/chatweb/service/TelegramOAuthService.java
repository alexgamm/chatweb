package chatweb.service;

import chatweb.configuration.properties.TelegramOAuthProperties;
import chatweb.exception.telegram.TelegramOAuthException;
import chatweb.model.telegram.TelegramOAuthResult;
import chatweb.utils.HashUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import static chatweb.utils.HashUtils.sha256;

@Service
@EnableConfigurationProperties(TelegramOAuthProperties.class)
@RequiredArgsConstructor
public class TelegramOAuthService {
    private final static String OAUTH_URI = "https://oauth.telegram.org/auth";
    private final ObjectMapper objectMapper;
    private final TelegramOAuthProperties telegramOAuthProperties;

    public TelegramOAuthResult parseOAuthResult(String authResult) throws TelegramOAuthException {
        TelegramOAuthResult result;
        String payload;
        try {
            Map<String, Object> fields = objectMapper.readValue(
                    new String(Base64.getDecoder().decode(authResult)),
                    objectMapper.getTypeFactory()
                            .constructParametricType(Map.class, String.class, Object.class)
            );
            result = objectMapper.convertValue(fields, TelegramOAuthResult.class);
            payload = fields.entrySet().stream()
                    .filter(en -> !en.getKey().equals("hash"))
                    .sorted(Map.Entry.comparingByKey())
                    .map(en -> en.getKey() + "=" + en.getValue())
                    .collect(Collectors.joining("\n"));
        } catch (Throwable e) {
            throw new TelegramOAuthException(e);
        }
        byte[] secretKey = sha256(telegramOAuthProperties.getBotToken());
        String hash = HashUtils.hmacSha256(payload, secretKey);
        if (!hash.equals(result.getHash())) {
            throw new TelegramOAuthException("invalid hash");
        }
        return result;
    }

    public String getOauthUrl() {
        try {
            return new URIBuilder(OAUTH_URI)
                    .addParameter("bot_id", telegramOAuthProperties.getBotId())
                    .addParameter("origin", telegramOAuthProperties.getRedirectUri())
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
