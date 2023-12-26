package chatweb.model.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class Event {
    public String getType() {
        return getClass().getSimpleName();
    }
}
