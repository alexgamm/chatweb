package chatweb.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RoomIdEvent implements IRoomEvent {
    @Setter
    private Integer roomId;
}
