package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.Getter;

import java.util.Date;

@Getter
public class EditedMessageEvent extends Event {
    private final MessageDto messageDto;

    public EditedMessageEvent(MessageDto messageDto) {
        super(EventType.EDITED_MESSAGE, new Date());
        this.messageDto = messageDto;
    }
}
