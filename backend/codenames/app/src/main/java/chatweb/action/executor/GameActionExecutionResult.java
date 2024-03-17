package chatweb.action.executor;

import chatweb.action.GameAction;
import chatweb.model.game.GameState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GameActionExecutionResult {
    private final GameState newState;
    private final boolean cancelScheduledTasks;
    @Builder.Default
    private final List<PostTask> postTasks = Collections.emptyList();

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class PostTask {
        private final GameAction action;
        @Nullable
        private final Instant startAt;

        public static PostTask immediate(GameAction action) {
            return new PostTask(action, null);
        }

        public static PostTask scheduled(GameAction action, Instant startAt) {
            return new PostTask(action, startAt);
        }
    }
}
