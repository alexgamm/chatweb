package chatweb.controller;

import chatweb.client.ChatApiClient;
import chatweb.entity.*;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.GameMapper;
import chatweb.model.api.*;
import chatweb.model.game.Settings;
import chatweb.model.game.state.Status;
import chatweb.repository.DictionaryRepository;
import chatweb.service.GameService;
import chatweb.utils.updaters.PickCard;
import chatweb.utils.updaters.RestartGame;
import chatweb.utils.updaters.StartGame;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static chatweb.model.api.ApiError.badRequest;
import static chatweb.model.api.ApiError.notFound;

@RestController
@RequestMapping("api/codenames/game")
@RequiredArgsConstructor
public class GameController implements ApiControllerHelper {
    private final ChatApiClient chatApiClient;
    private final DictionaryRepository dictionaryRepository;
    private final GameService gameService;
    private final GameMapper gameMapper;

    @GetMapping("{gameId}")
    public GameDto getGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        Room room = game.getRoom();
        if (!room.getUsers().contains(user)) {
            throw badRequest("You are not a member of this room").toException();
        }
        return gameMapper.map(user.getId(), game);
    }


    @PostMapping
    public CreateGameResponse createGame(
            @RequestAttribute User user,
            @RequestBody CreateGameRequest request
    ) throws ApiErrorException {
        if (request.getDictionaryId() == null) {
            throw badRequest("Dictionary id is missing").toException();
        }
        Dictionary dictionary = dictionaryRepository.findById(request.getDictionaryId()).orElse(null);
        if (dictionary == null) {
            throw badRequest("Dictionary is not found").toException();
        }
        CreateRoomResponse roomResponse = chatApiClient.createRoom(new CreateRoomRequest(
                user.getId(),
                "codenames",
                request.getPassword()
        ));
        Game game = gameService.createGame(roomResponse.getKey(), roomResponse.getId(), user, dictionary);
        return new CreateGameResponse(game.getId());
    }

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

    @PostMapping("{gameId}/start")
    public ApiResponse startGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        if (!game.isPlayer(user)) {
            throw badRequest("You are not a player in this game").toException();
        }
        if (!game.getHost().equals(user)) {
            throw badRequest("Only host may start the game").toException();
        }
        if (game.getState().getStatus() == Status.ACTIVE) {
            throw badRequest("Game is already started").toException();
        }
        for (Team team : game.getTeams()) {
            if (team.getLeader() == null || team.getPlayers().isEmpty()) {
                throw badRequest("Each team must have a leader and at least one player").toException();
            }
        }
        gameService.executeAction(game, new StartGame(Status.ACTIVE));
        return new ApiResponse(true);
    }

    @PostMapping("{gameId}/pick")
    public ApiResponse pickCard(
            @PathVariable String gameId,
            @RequestAttribute User user,
            PickCardRequest request
    ) throws ApiErrorException {
        // TODO all team members should pick at the same card to invoke this method
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        Team userTeam = game.getTeams().stream()
                .filter(team -> team.getPlayers().contains(user))
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
                    game.getSettings().getTurnSeconds())
            );
        } catch (IllegalStateException e) {
            throw badRequest(e.getMessage()).toException();
        }
        return new ApiResponse(true);
    }

    @PostMapping("{gameId}/restart")
    public ApiResponse restartGame(@PathVariable String gameId, @RequestAttribute User user) throws ApiErrorException {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        if (!game.getHost().equals(user)) {
            throw badRequest("Only host may restart the game").toException();
        }
        Dictionary dictionary = dictionaryRepository.findById(game.getSettings().getDictionaryId()).orElse(null);
        if (dictionary == null) {
            throw notFound("Dictionary is not found").toException();
        }
        gameService.executeAction(game, new RestartGame(game, dictionary));
        return new ApiResponse(true);
    }

    @PatchMapping("{gameId}/editSettings")
    public ApiResponse editSettings(
            @PathVariable String gameId,
            @RequestAttribute User user,
            @RequestBody @Validated ChangeSettingsRequest request
    ) throws ApiErrorException {
        Game game = gameService.findGame(gameId);
        if (game == null) {
            throw notFound("Game is not found").toException();
        }
        if (!game.getHost().equals(user)) {
            throw badRequest("Only host may change the settings").toException();
        }
        Settings.SettingsBuilder copy = game.getSettings().copy();
        if (request.getTurnSeconds() != null) {
            copy.turnSeconds(request.getTurnSeconds());
        }
        if (request.getDictionaryId() != null) {
            copy.dictionaryId(request.getDictionaryId());
        }
        if (request.getBoardSize() != null) {
            copy.boardSize(request.getBoardSize());
        }
        Settings newSettings = copy.build();
        gameService.changeSettings(gameId, newSettings);
        Dictionary dictionary = dictionaryRepository.findById(newSettings.getDictionaryId()).orElse(null);
        if (dictionary == null) {
            throw notFound("Dictionary is not found").toException();
        }
        gameService.executeAction(game, new RestartGame(game, dictionary));
        return new ApiResponse(true);
    }

}