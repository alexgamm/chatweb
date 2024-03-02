package chatweb.controller;

import chatweb.client.ChatApiClient;
import chatweb.entity.Dictionary;
import chatweb.entity.Game;
import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.GameMapper;
import chatweb.model.api.*;
import chatweb.repository.DictionaryRepository;
import chatweb.service.GameService;
import lombok.RequiredArgsConstructor;
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
        return gameMapper.map(user, game);
    }


    @PostMapping
    public CreateGameResponse createGame(
            @RequestAttribute User user,
            @RequestBody CreateGameRequest request
    ) throws ApiErrorException {
        // TODO Validate all first, then create the room
        CreateRoomResponse roomResponse = chatApiClient.createRoom(new CreateRoomRequest(
                user.getId(),
                "codenames",
                request.getPassword()
        ));
        if (request.getDictionaryId() == null) {
            throw badRequest("Dictionary id is missing").toException();
        }
        Dictionary dictionary = dictionaryRepository.findById(request.getDictionaryId()).orElse(null);
        if (dictionary == null) {
            throw badRequest("Dictionary is not found").toException();
        }
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
}