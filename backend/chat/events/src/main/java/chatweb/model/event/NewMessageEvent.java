package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewMessageEvent extends RoomIdEvent {
    private MessageDto message;

    public NewMessageEvent(Integer roomId, MessageDto message) {
        super(roomId);
        this.message = message;
    }
}
