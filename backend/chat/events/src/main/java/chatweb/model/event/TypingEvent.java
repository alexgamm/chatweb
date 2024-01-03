package chatweb.model.event;

import chatweb.exception.InvalidRoomKeyException;
import chatweb.utils.RoomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TypingEvent implements IRoomEvent {
    private int userId;
    @Nullable
    private String room;

    @Override
    public Integer getRoomId() {
        if (room == null) return null;
        try {
            return RoomUtils.decodeKey(room);
        } catch (InvalidRoomKeyException e) {
            return null;
        }
    }

    @Override
    public void setRoomId(Integer roomId) {
    }
}
