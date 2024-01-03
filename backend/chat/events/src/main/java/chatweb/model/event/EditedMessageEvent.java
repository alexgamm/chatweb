package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EditedMessageEvent extends RoomIdEvent {
    private MessageDto message;

    public EditedMessageEvent(Integer roomId, MessageDto message) {
        super(roomId);
        this.message = message;
    }
}
