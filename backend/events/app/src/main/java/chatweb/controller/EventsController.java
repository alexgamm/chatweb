package chatweb.controller;

import chatweb.entity.User;
import chatweb.exception.UnauthorizedException;
import chatweb.helper.UserAuthHelper;
import chatweb.mapper.GameMapper;
import chatweb.mapper.MessageMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.OnlineResponse;
import chatweb.model.event.*;
import chatweb.service.EventsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController implements ApiControllerHelper {
    private final EventsService eventsService;
    private final UserAuthHelper userAuthHelper;

    @GetMapping("stream")
    public SseEmitter streamEvents(HttpServletRequest request) throws IOException {
        User user;
        try {
            user = userAuthHelper.auth(request);
        } catch (UnauthorizedException ex) {
            SseEmitter emitter = new SseEmitter();
            emitter.send(
                    SseEmitter.event()
                            .name("error")
                            .data(new ApiError(HttpStatus.UNAUTHORIZED, "unauthorized"))
            );
            emitter.complete();
            return emitter;
        }
        return eventsService.createEmitter(user.getId());
    }

    @GetMapping("online")
    public OnlineResponse getOnline() {
        return new OnlineResponse(eventsService.getOnlineUserIds());
    }

    @PostMapping
    public IEvent addEvent(@RequestBody IEvent event) {
        if (event instanceof ServiceReactionEvent serviceReactionEvent) {
            eventsService.addEvent((userId) -> new ReactionEvent(
                    serviceReactionEvent.getRoom(),
                    serviceReactionEvent.getMessageId(),
                    MessageMapper.groupReactions(serviceReactionEvent.getReactions(), userId)
            ));
        } else if (event instanceof ServiceGameStateChangedEvent serviceGameStateChangedEvent) {
            eventsService.addEvent((userId) -> new GameStateChangedEvent(
                    serviceGameStateChangedEvent.getGame().getId(),
                    GameMapper.mapState(userId, serviceGameStateChangedEvent.getGame())
            ));
        } else {
            eventsService.addEvent(event);
        }
        return event;
    }
}

