package chatweb.model.event;

import chatweb.entity.Game;
import chatweb.mapper.GameMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServiceGameStateChangedEvent extends RoomEvent implements PersonalEventProducer {
    private final Game game;

    @Override
    public IEvent getPersonalEvent(Integer userId) {
        return new GameStateChangedEvent(
                getGame().getId(),
                GameMapper.mapState(userId, getGame())
        );
    }
}
