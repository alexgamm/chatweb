package chatweb.model.game.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class Turn {
    private Integer teamId;
    private Integer durationSeconds;
    @Nullable
    private Instant startAt;
}
