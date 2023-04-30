package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.longpoll.LongPollFuture;
import chatweb.model.Event;
import chatweb.model.EventsResponse;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.EventsService;
import webserver.Request;
import webserver.RequestFailedException;

import java.util.List;

public class EventsEndpoint extends AuthEndpoint{
    private final EventsService eventsService;

    public EventsEndpoint(UserRepository userRepository, SessionRepository sessionRepository, EventsService eventsService) {
        super(userRepository, sessionRepository);
        this.eventsService = eventsService;
    }

    @Override
    public Object authGet(Request request, User user) throws RequestFailedException {
        long ts;
        try {
            ts = Long.parseLong(request.getQuery().get("ts"));
        } catch (Throwable e) {
            throw new RequestFailedException(400, "incorrect ts");
        }
        List<Event> events = eventsService.getEvents(ts);
        LongPollFuture longPollFuture = new LongPollFuture(ts);
        if (events.isEmpty()) {
            eventsService.addLongPollFuture(longPollFuture);
        } else {
            longPollFuture.complete(events);
        }
        userRepository.updateLastActivityAt(user.getId());
        return longPollFuture.thenApply(eventList -> new EventsResponse(eventList));
    }
}
