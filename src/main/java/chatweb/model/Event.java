package chatweb.model;

import java.util.Date;

public abstract class Event {
    private final EventType type;
    private final Date date = new Date();

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
