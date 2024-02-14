package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditedMessageEvent implements IRoomEvent {
    private MessageDto message;

    @Override
    public @Nullable String getRoom() {
        return message.getRoom();
    }
}
