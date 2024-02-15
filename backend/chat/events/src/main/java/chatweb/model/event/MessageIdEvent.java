package chatweb.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class MessageIdEvent extends RoomEvent {
    private String messageId;

    public MessageIdEvent(String room, String messageId) {
        super(room);
        this.messageId = messageId;
    }
}
