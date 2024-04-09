package chatweb.controller;

import chatweb.mapper.MessageMapper;
import chatweb.model.api.OnlineResponse;
import chatweb.model.event.IEvent;
import chatweb.model.event.ReactionEvent;
import chatweb.model.event.ServiceReactionEvent;
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
        if (event instanceof ServiceReactionEvent serviceReactionEvent) {
            eventsService.addEvent((userId) -> new ReactionEvent(
                    serviceReactionEvent.getRoom(),
                    serviceReactionEvent.getMessageId(),
                    MessageMapper.groupReactions(serviceReactionEvent.getReactions(), userId)
            ));
        } else {
            eventsService.addEvent(event);
        }
        return event;
    }
}

