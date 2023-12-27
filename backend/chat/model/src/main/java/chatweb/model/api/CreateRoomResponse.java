package chatweb.model.api;

import chatweb.entity.Room;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateRoomResponse {
    private final Room room;
}
