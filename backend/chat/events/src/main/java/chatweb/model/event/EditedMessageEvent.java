package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditedMessageEvent extends Event {
    private MessageDto message;
}
