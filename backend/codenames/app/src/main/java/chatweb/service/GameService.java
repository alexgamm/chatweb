package chatweb.service;

import chatweb.action.ChangeTurn;
import chatweb.action.GameAction;
import chatweb.action.PickCard;
import chatweb.action.RestartGame;
import chatweb.action.StartGame;
import chatweb.action.executor.GameActionExecutionResult;
import chatweb.action.executor.GameActionExecutor;
import chatweb.client.EventsApiClient;
import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import chatweb.entity.Room;
import chatweb.entity.Team;
import chatweb.entity.User;
import chatweb.model.Color;
import chatweb.model.event.ServiceGameStateChangedEvent;
import chatweb.model.game.Settings;
import chatweb.repository.GameRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GameService {
    private final GameRepository gameRepository;

    private final Map<Class<? extends GameAction>, GameActionExecutor<? extends GameAction>> actionExecutors;
    private final GameSchedulingService gameSchedulingService;
    private final EventsApiClient eventsApi;


    public GameService(
            GameRepository gameRepository,
            GameActionExecutor<PickCard> pickCardExecutor,
            GameActionExecutor<ChangeTurn> changeTurnExecutor,
            GameActionExecutor<StartGame> startGameExecutor, GameSchedulingService gameSchedulingService, EventsApiClient eventsApi
    ) {
        this.gameRepository = gameRepository;
        this.gameSchedulingService = gameSchedulingService;
        this.eventsApi = eventsApi;
        this.actionExecutors = Map.of(
                PickCard.class, pickCardExecutor,
                ChangeTurn.class, changeTurnExecutor,
                StartGame.class, startGameExecutor
        );
    }

    @NotNull
    public Game createGame(String id, Integer roomId, User host, Dictionary dictionary) {
        Settings defaultSettings = Settings.getDefault(dictionary.getId());
        Game game = new Game(
                id,
                new Room(roomId, null, null, null, null),
                host,
                List.of(host),
                Collections.emptyList(),
                defaultSettings,
                null
        );
        game.setTeams(List.of(
                new Team(null, game, null, Color.FUCHSIA, null),
                new Team(null, game, null, Color.BLUE, null)
        ));
        game = gameRepository.save(game);
        executeAction(game, new RestartGame(game));
        return game;
    }

    @Nullable
    public Game findGame(String id) {
        return gameRepository.findById(id).orElse(null);
    }

    public void addViewer(Game game, User viewer) {
        game.getViewers().add(viewer);
        gameRepository.save(game);
    }

    public void removeViewer(Game game, User viewer) {
        game.getViewers().remove(viewer);
        gameRepository.save(game);
    }

    public <T extends GameAction> void executeAction(Game game, T action) {
        //noinspection unchecked
        GameActionExecutor<T> executor = (GameActionExecutor<T>) actionExecutors.get(action.getClass());
        if (executor == null) {
            throw new IllegalArgumentException(String.format("No executor found for action type: %s", action.getClass()));
        }
        GameActionExecutionResult result = executor.execute(game.getState(), action);
        gameRepository.updateState(game.getId(), result.getNewState());
        eventsApi.addEvent(new ServiceGameStateChangedEvent(game));
        if (result.isCancelScheduledTask()) {
            gameSchedulingService.cancelTaskIfExists(game.getId());
        }
        result.getPostTasks().forEach(task -> {
            if (task.getStartAt() != null) {
                gameSchedulingService.schedule(
                        game.getId(),
                        () -> gameRepository.findById(game.getId()).ifPresent(g -> executeAction(g, task.getAction())),
                        task.getStartAt()
                );
            } else {
                executeAction(game, task.getAction());
            }
        });
    }

    public void updateSettings(String gameId, Settings settings) {
        gameRepository.updateSettings(gameId, settings);
    }

}
