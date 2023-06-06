package chatweb.model;

import java.util.Date;

public abstract class Event {
    private final EventType type;
    private final Date date;

    public Event(EventType type, Date date) {
        this.type = type;
        this.date = date;
    }

    public EventType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
