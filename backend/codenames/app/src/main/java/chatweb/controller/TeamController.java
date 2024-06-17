package chatweb.controller;

import chatweb.action.PauseGame;
import chatweb.entity.Game;
import chatweb.entity.Team;
import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.ApiResponse;
import chatweb.model.api.JoinTeamRequest;
import chatweb.model.event.ServiceGameUpdatedEvent;
import chatweb.model.game.state.Status;
import chatweb.producer.EventsKafkaProducer;
import chatweb.service.GameService;
import chatweb.service.TeamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static chatweb.model.api.ApiError.badRequest;

@RestController
@RequestMapping("api/codenames/team")
@RequiredArgsConstructor
public class TeamController implements ApiControllerHelper {

    private final TeamService teamService;
    private final GameService gameService;
    private final EventsKafkaProducer eventsProducer;

    @Transactional
    @PostMapping("{teamId}/join")
    public ApiResponse joinTeam(
            @PathVariable Integer teamId,
            @RequestAttribute User user,
            @RequestBody JoinTeamRequest request
    ) throws ApiErrorException {
        Team team = teamService.findTeam(teamId);
        if (team == null) {
            throw badRequest("Required team doesn't exist").toException();
        }
        Game game = team.getGame();
        if (!game.getRoom().getUsers().contains(user)) {
            throw badRequest("You are not a member of this room").toException();
        }
        if (game.getState().getStatus() == Status.ACTIVE) {
            throw ApiError.forbidden(String.format(
                    "You may not become %s while the game is in progress",
                    request.isLeader() ? "leader" : "member"
            )).toException();
        }
        if (request.isLeader()) {
            if (team.isLeader(user)) {
                throw badRequest("You are already a leader").toException();
            }
            if (team.getLeader() != null) {
                throw ApiError.forbidden("There can only be one leader in a team").toException();
            }
        } else if (team.isPlayer(user) && !team.isLeader(user)) {
            throw badRequest("You are already a member").toException();
        }
        gameService.removeViewer(game, user);
        teamService.addPlayer(team, user, request.isLeader());
        eventsProducer.addEvent(new ServiceGameUpdatedEvent(gameService.findGame(game.getId())));
        return new ApiResponse(true);
    }

    @Transactional
    @PostMapping("{teamId}/leave")
    public ApiResponse leaveTeam(@PathVariable Integer teamId, @RequestAttribute User user) throws ApiErrorException {
        Team team = teamService.findTeam(teamId);
        if (team == null) {
            throw badRequest("Required team doesn't exist").toException();
        }
        Game game = team.getGame();
        if (!team.isPlayer(user)) {
            throw badRequest("You are not a member of this team").toException();
        }
        if ((team.isLeader(user) || team.getPlayers().size() == 1) && game.getState().getStatus() == Status.ACTIVE) {
            gameService.executeAction(game, new PauseGame());
        }
        teamService.removePlayer(team, user);
        gameService.addViewer(game, user);
        return new ApiResponse(true);
    }
}
