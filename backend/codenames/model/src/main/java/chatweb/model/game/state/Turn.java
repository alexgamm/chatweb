package chatweb.model.game.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class Turn {
    private Integer teamId;
    private boolean leader;
    private Integer durationSeconds;
    @Nullable
    private Instant startedAt;
}
