package chatweb.model.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChangeUserColorEvent.class, name = "CHANGE_COLOR"),
        @JsonSubTypes.Type(value = ChangeUsernameEvent.class, name = "CHANGE_USERNAME"),
        @JsonSubTypes.Type(value = DeletedMessageEvent.class, name = "DELETED_MESSAGE"),
        @JsonSubTypes.Type(value = EditedMessageEvent.class, name = "EDITED_MESSAGE"),
        @JsonSubTypes.Type(value = NewMessageEvent.class, name = "NEW_MESSAGE"),
        @JsonSubTypes.Type(value = ReactionEvent.class, name = "REACTION"),
        @JsonSubTypes.Type(value = ServiceReactionEvent.class, name = "SERVICE_REACTION"),
        @JsonSubTypes.Type(value = UserOnlineEvent.class, name = "USER_ONLINE"),
        @JsonSubTypes.Type(value = UserTypingEvent.class, name = "USER_TYPING")
})
public abstract class Event {
    private final EventType type;
    // TODO check if the field is required
    private final Date date;
    private final Integer roomId;

    public Event(EventType type, Date date, Integer roomId) {
        this.type = type;
        this.date = date;
        this.roomId = roomId;
    }

    public EventType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Integer getRoomId() {
        return roomId;
    }
}
