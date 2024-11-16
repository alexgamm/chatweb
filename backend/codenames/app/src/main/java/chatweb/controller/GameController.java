package chatweb.controller;

import chatweb.action.ChangeTurn;
import chatweb.action.PauseGame;
import chatweb.action.PickCard;
import chatweb.action.ResetGame;
import chatweb.action.StartGame;
import chatweb.chat.ChatServiceOuterClass;
import chatweb.client.ChatRpcClient;
import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import chatweb.entity.Room;
import chatweb.entity.Team;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.GameMapper;
import chatweb.mapper.GameStateMapper;
import chatweb.model.api.ApiResponse;
import chatweb.model.api.CreateGameRequest;
import chatweb.model.api.CreateGameResponse;
import chatweb.model.api.GameDto;
import chatweb.model.api.PickCardRequest;
import chatweb.model.api.UpdateSettingsRequest;
import chatweb.model.game.GameState;
import chatweb.model.game.Settings;
import chatweb.model.game.state.Status;
import chatweb.model.message.LinkButton;
import chatweb.repository.DictionaryRepository;
import chatweb.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static chatweb.model.api.ApiError.badRequest;
import static chatweb.model.api.ApiError.notFound;

@RestController
@RequestMapping("api/codenames/game")
@RequiredArgsConstructor
public class GameController implements ApiControllerHelper {
    private final ChatRpcClient chatRpcClient;
    private final DictionaryRepository dictionaryRepository;
    private final GameService gameService;

    @GetMapping("{gameId}")
    public GameDto getGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        return GameMapper.INSTANCE.toDto(findGame(gameId, user));
    }

    @GetMapping("{gameId}/state")
    public GameState getGameState(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        return GameStateMapper.INSTANCE.toPersonalState(user.getId(), findGame(gameId, user));
    }

    @NotNull
    private Game findGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        Room room = game.getRoom();
        if (!room.getUsers().contains(user)) {
            throw badRequest("You are not a member of this room").toException();
        }
        return game;
    }


    @Transactional
    @PostMapping
    public CreateGameResponse createGame(
            @RequestAttribute User user,
            @RequestBody CreateGameRequest request
    ) throws ApiErrorException {
        Dictionary dictionary = dictionaryRepository.findById(request.getDictionaryId()).orElse(null);
        if (dictionary == null) {
            throw badRequest("Dictionary is not found").toException();
        }
        ChatServiceOuterClass.CreateRoomResponse roomResponse = chatRpcClient.createRoom(
                user.getId(),
                "codenames",
                Optional.ofNullable(request.getPassword())
                        .filter(password -> !password.isBlank())
                        .orElse(null)
        );
        Game game = gameService.createGame(roomResponse.getKey(), roomResponse.getId(), user, dictionary);
        chatRpcClient.sendMessage(
                user.getId(),
                "Come play Codenames with me!",
                List.of(new LinkButton("Join", String.format("/room/%s", game.getRoom().getKey())))
        );
        return new CreateGameResponse(game.getId());
    }

    @Transactional
    @PostMapping("{gameId}/join")
    public ApiResponse joinGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        if (game.isPlayer(user) || game.isViewer(user)) {
            throw badRequest("You are already in the room").toException();
        }
        gameService.addViewer(game, user);
        return new ApiResponse(true);
    }

    @Transactional
    @PostMapping("{gameId}/start")
    public ApiResponse startGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = findGame(gameId, user);
        if (!user.equals(game.getHost())) {
            throw badRequest("Only host may start the game").toException();
        }
        if (game.getState().getStatus() == Status.ACTIVE) {
            throw badRequest("Game is already started").toException();
        }
        if (game.getTeams().stream().anyMatch(team -> team.getLeader() == null || team.getPlayers().isEmpty())) {
            throw badRequest("Each team must have a leader and at least one player").toException();
        }
        if (game.getState().getStatus() == Status.FINISHED) {
            gameService.executeAction(game, new ResetGame(game));
        }
        gameService.executeAction(game, new StartGame(game.getSettings().turnSeconds()));
        return new ApiResponse(true);
    }

    @Transactional
    @PostMapping("{gameId}/pause")
    public ApiResponse pauseGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = findGame(gameId, user);
        if (!user.equals(game.getHost())) {
            throw badRequest("Only host may pause the game").toException();
        }
        if (game.getState().getStatus() != Status.ACTIVE) {
            throw badRequest("Game is not active").toException();
        }
        gameService.executeAction(game, new PauseGame());
        return new ApiResponse(true);
    }

    @Transactional
    @PostMapping("{gameId}/cards/pick")
    public ApiResponse pickCard(
            @PathVariable String gameId,
            @RequestAttribute User user,
            @RequestBody PickCardRequest request
    ) throws ApiErrorException {
        // TODO all team members should pick the same card to invoke this method
        Game game = findGame(gameId, user);
        Team userTeam = game.getTeams().stream()
                .filter(team -> team.isPlayer(user))
                .findFirst()
                .orElse(null);
        if (userTeam == null) {
            throw badRequest("You are not a player in this game").toException();
        }
        if (userTeam.isLeader(user)) {
            throw badRequest("Only team members can pick cards").toException();
        }
        try {
            gameService.executeAction(game, new PickCard(
                    request.getWord(),
                    userTeam.getId(),
                    game.getSettings().turnSeconds())
            );
        } catch (IllegalStateException e) {
            throw badRequest(e.getMessage()).toException();
        }
        return new ApiResponse(true);
    }

    @Transactional
    @PostMapping("{gameId}/turn/end")
    public ApiResponse endTurn(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = findGame(gameId, user);
        if (game.getState().getStatus() != Status.ACTIVE) {
            throw badRequest("Game is not active").toException();
        }
        Team userTeam = game.getTeams().stream()
                .filter(team -> team.isPlayer(user))
                .findFirst()
                .orElse(null);
        if (userTeam == null) {
            throw badRequest("You are not a player in this game").toException();
        }
        if (game.getState().getTurn().isLeader() && !userTeam.isLeader(user)) {
            throw badRequest("Now it's the leader's turn").toException();
        }
        if (!game.getState().getTurn().isLeader() && userTeam.isLeader(user)) {
            throw badRequest("Now it's the team members' turn").toException();
        }
        gameService.executeAction(game, new ChangeTurn(game.getSettings().turnSeconds()));
        return new ApiResponse(true);
    }

    @Transactional
    @PostMapping("{gameId}/reset")
    public ApiResponse resetGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = findGame(gameId, user);
        if (!user.equals(game.getHost())) {
            throw badRequest("Only host may reset the game").toException();
        }
        gameService.executeAction(game, new ResetGame(game));
        return new ApiResponse(true);
    }

    @Transactional
    @PatchMapping("{gameId}/settings")
    public ApiResponse updateSettings(
            @PathVariable String gameId,
            @RequestAttribute User user,
            @RequestBody @Validated UpdateSettingsRequest request
    ) throws ApiErrorException {
        Game game = findGame(gameId, user);
        if (!user.equals(game.getHost())) {
            throw badRequest("Only host may change the settings").toException();
        }
        Settings.SettingsBuilder copy = game.getSettings().copy();
        if (request.getTurnSeconds() != null) {
            copy.turnSeconds(request.getTurnSeconds());
        }
        if (request.getDictionaryId() != null) {
            if (!dictionaryRepository.existsById(request.getDictionaryId())) {
                throw badRequest("Dictionary is not found").toException();
            }
            copy.dictionaryId(request.getDictionaryId());
        }
        if (request.getBoardSize() != null) {
            copy.boardSize(request.getBoardSize());
        }
        Settings newSettings = copy.build();
        gameService.updateSettings(gameId, newSettings);
        gameService.executeAction(game, new ResetGame(game));
        return new ApiResponse(true);
    }

}