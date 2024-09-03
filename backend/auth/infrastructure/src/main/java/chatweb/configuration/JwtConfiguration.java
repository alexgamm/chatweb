package chatweb.configuration;

import chatweb.configuration.properties.JwtProperties;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty("jwt.secret")
public class JwtConfiguration {
    @Bean
    public Key jwtKey(JwtProperties jwtProperties) {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public JwtParser jwtParser(Key jwtKey) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build();
    }
}
