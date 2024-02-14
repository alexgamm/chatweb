package chatweb.model.event;

import chatweb.exception.InvalidRoomKeyException;
import chatweb.utils.RoomUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Nullable;

public interface IRoomEvent extends IEvent {
    @Nullable
    String getRoom();

    @Nullable
    @JsonIgnore
    default Integer getRoomId() {
        if (getRoom() == null) {
            return null;
        }
        try {
            return RoomUtils.idFromKey(getRoom());
        } catch (InvalidRoomKeyException e) {
            return null;
        }
    }
}
