package chatweb.controller;

import chatweb.entity.User;
import chatweb.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController implements ApiControllerHelper {
    private final EventsService eventsService;

    @GetMapping("stream")
    public SseEmitter streamEvents(@RequestAttribute User user) {
        return eventsService.createEmitter(user.getId());
    }
}

