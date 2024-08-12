package chatweb.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "ResetPasswordSession")
@Accessors(chain = true)
@Getter
@Setter
public class ResetPasswordSession {
    private String id;
    private String email;
    @TimeToLive
    private Long ttlSeconds;
}
