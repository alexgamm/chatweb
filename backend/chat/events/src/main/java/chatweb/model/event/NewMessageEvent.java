package chatweb.model.event;

import chatweb.model.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewMessageEvent implements IRoomEvent {
    private MessageDto message;

    @Nullable
    @Override
    public String getRoom() {
        return message.getRoom();
    }
}
