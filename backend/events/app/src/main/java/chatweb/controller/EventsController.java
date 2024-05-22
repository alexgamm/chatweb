package chatweb.controller;

import chatweb.model.api.OnlineResponse;
import chatweb.model.event.IEvent;
import chatweb.model.event.PersonalEventProducer;
import chatweb.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController implements ApiControllerHelper {
    private final EventsService eventsService;

    @GetMapping("online")
    public OnlineResponse getOnline() {
        return new OnlineResponse(eventsService.getOnlineUserIds());
    }

    @PostMapping
    public IEvent addEvent(@RequestBody IEvent event) {
        if (event instanceof PersonalEventProducer personalEventProducer) {
            eventsService.addEvent(personalEventProducer);
        } else {
            eventsService.addEvent(event);
        }
        return event;
    }
}

