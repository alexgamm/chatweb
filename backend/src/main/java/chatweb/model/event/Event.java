package chatweb.model.event;

import java.util.Date;

public abstract class Event {
    private final EventType type;
    // TODO check if the field is required
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
