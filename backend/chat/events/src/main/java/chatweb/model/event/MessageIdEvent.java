package chatweb.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class MessageIdEvent extends RoomIdEvent {
    private String messageId;

    public MessageIdEvent(Integer roomId, String messageId) {
        super(roomId);
        this.messageId = messageId;
    }
}
