package chatweb.service;

import chatweb.action.ChangeTurn;
import chatweb.action.EndGame;
import chatweb.action.GameAction;
import chatweb.action.GameActionExecutionResult;
import chatweb.action.PauseGame;
import chatweb.action.PickCard;
import chatweb.action.ResetGame;
import chatweb.action.StartGame;
import chatweb.action.executor.GameActionExecutor;
import chatweb.client.EventsRpcClient;
import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import chatweb.entity.Room;
import chatweb.entity.Team;
import chatweb.entity.User;
import chatweb.model.Color;
import chatweb.model.event.ServiceGameUpdatedEvent;
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
    private final EventsRpcClient eventsRpc;


    public GameService(
            GameRepository gameRepository,
            GameSchedulingService gameSchedulingService,
            EventsRpcClient eventsRpc,
            GameActionExecutor<ChangeTurn> changeTurnExecutor,
            GameActionExecutor<EndGame> endGameExecutor,
            GameActionExecutor<PauseGame> pauseGameExecutor,
            GameActionExecutor<PickCard> pickCardExecutor,
            GameActionExecutor<ResetGame> resetGameExecutor,
            GameActionExecutor<StartGame> startGameExecutor
    ) {
        this.gameRepository = gameRepository;
        this.gameSchedulingService = gameSchedulingService;
        this.eventsRpc = eventsRpc;
        this.actionExecutors = Map.of(
                ChangeTurn.class, changeTurnExecutor,
                EndGame.class, endGameExecutor,
                PauseGame.class, pauseGameExecutor,
                PickCard.class, pickCardExecutor,
                ResetGame.class, resetGameExecutor,
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
        executeAction(game, new ResetGame(game));
        return game;
    }

    @Nullable
    public Game findGame(String id) {
        return gameRepository.findById(id).orElse(null);
    }

    public void addViewer(Game game, User viewer) {
        game.getViewers().add(viewer);
        eventsRpc.addEvent(new ServiceGameUpdatedEvent(game));
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
        game.setState(result.getNewState());
        eventsRpc.addEvent(new ServiceGameUpdatedEvent(game));
        if (result.isCancelScheduledTask()) {
            gameSchedulingService.cancelTaskIfExists(game.getId());
        }
        String gameId = game.getId();
        for (GameActionExecutionResult.PostTask task : result.getPostTasks()) {
            if (task.getStartAt() != null) {
                gameSchedulingService.schedule(
                        game.getId(),
                        () -> gameRepository.findById(gameId).ifPresent(g -> executeAction(g, task.getAction())),
                        task.getStartAt()
                );
            } else {
                executeAction(game, task.getAction());
            }
        }
    }

    public void updateSettings(String gameId, Settings settings) {
        gameRepository.updateSettings(gameId, settings);
    }

}
