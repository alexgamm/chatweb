package chatweb.controller;

import chatweb.entity.User;
import chatweb.longpoll.LongPollFuture;
import chatweb.model.api.EventsResponse;
import chatweb.model.event.Event;
import chatweb.model.event.UserActivity;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController implements ApiControllerHelper {
    private final EventsService eventsService;
    private final UserRepository userRepository;

    @GetMapping
    @Transactional
    public CompletableFuture<EventsResponse> getEvents(@RequestParam long ts, HttpServletRequest request) {
        List<Event> events = eventsService.getEvents(ts);
        LongPollFuture longPollFuture = new LongPollFuture(ts);
        if (events.isEmpty()) {
            eventsService.addLongPollFuture(longPollFuture);
        } else {
            longPollFuture.complete(events);
        }
        User user = (User) request.getAttribute("user");
        userRepository.updateLastActivityAt(user.getId());
        if (eventsService.getEvents().stream()
                .noneMatch(event -> event instanceof UserActivity
                        && ((UserActivity) event).getUsername().equals(user.getUsername())
                        && !((UserActivity) event).isExpired())) {
            eventsService.addEvent(new UserActivity(user.getUsername()));
        }
        return longPollFuture.thenApply(eventList -> new EventsResponse(eventList));
    }

    @GetMapping("stream")
    public SseEmitter streamEvents(@RequestAttribute User user) {
        return eventsService.createEmitter(user.getId());
    }
}

