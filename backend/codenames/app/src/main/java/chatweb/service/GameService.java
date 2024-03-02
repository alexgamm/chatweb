package chatweb.service;

import chatweb.entity.*;
import chatweb.model.Color;
import chatweb.model.game.BoardSize;
import chatweb.model.game.GameState;
import chatweb.model.game.Settings;
import chatweb.model.game.state.CardType;
import chatweb.model.game.state.Status;
import chatweb.model.game.state.Card;
import chatweb.model.game.state.Turn;
import chatweb.repository.GameRepository;
import chatweb.utils.GameUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

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
                new Team(null, game, null, Color.VIOLET, null),
                new Team(null, game, null, Color.BLUE, null)
        ));
        // TODO check if it really works without flush()
        game = gameRepository.save(game);
        game.setState(createState(game, dictionary));
        return gameRepository.save(game);
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

    public GameState createState(Game game, Dictionary dictionary) {
        List<Integer> shuffledTeamIds = game.getShuffledTeams().stream().map(Team::getId).toList();
        Settings settings = game.getSettings();
        List<Card> cards = GameUtils.createCards(
                dictionary.getRandomWords(settings.getBoardSize().getWordAmount()),
                shuffledTeamIds
        );
        Turn turn = new Turn(shuffledTeamIds.get(0), settings.getTurnSeconds() * 2, null);
        return new GameState(Status.IDLE, cards, shuffledTeamIds, turn);
    }
}
