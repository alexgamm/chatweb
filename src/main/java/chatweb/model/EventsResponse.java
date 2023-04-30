package chatweb.model;

import java.util.List;

public class EventsResponse {
    private final List<Event> events;

    public EventsResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
