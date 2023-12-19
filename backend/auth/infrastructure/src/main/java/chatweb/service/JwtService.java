package chatweb.service;

import chatweb.configuration.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;
    private final JwtParser jwtParser;
    private final Key jwtKey;

    public String createToken(int userId) {
        return Jwts.builder()
                .setId(Integer.toString(userId))
                .setExpiration(Date.from(Instant.now().plus(jwtProperties.getTokenLifetimeSeconds(), ChronoUnit.SECONDS)))
                .signWith(jwtKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public int decodeToken(String token) {
        Jws<Claims> parsedJwt = jwtParser.parseClaimsJws(token);
        int id;
        try {
            id = Integer.parseInt(parsedJwt.getBody().getId());
        } catch (Throwable e) {
            throw new MalformedJwtException(e.getMessage(), e);
        }
        return id;
    }
}
