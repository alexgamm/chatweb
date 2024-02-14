package chatweb.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RoomEvent implements IRoomEvent {
    @Nullable
    private String room;
}
