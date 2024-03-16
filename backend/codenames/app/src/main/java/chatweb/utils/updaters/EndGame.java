package chatweb.utils.updaters;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public class EndGame implements GameAction {
    @Nullable
    private final Integer lostTeamId;
    @Nullable
    private final Integer wonTeamId;
}
