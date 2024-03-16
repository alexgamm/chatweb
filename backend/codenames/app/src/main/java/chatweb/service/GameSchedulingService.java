package chatweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class GameSchedulingService {

    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> gameTasks = Collections.synchronizedMap(new HashMap<>());

    public void cancelTaskIfExists(String gameId) {
        ScheduledFuture<?> task = gameTasks.get(gameId);
        if (task != null) {
            task.cancel(true);
            gameTasks.remove(gameId);
        }
    }

    public void schedule(String gameId, Runnable action, Instant startTime) {
        cancelTaskIfExists(gameId);
        ScheduledFuture<?> task = taskScheduler.schedule(action, startTime);
        gameTasks.put(gameId, task);
    }

}
