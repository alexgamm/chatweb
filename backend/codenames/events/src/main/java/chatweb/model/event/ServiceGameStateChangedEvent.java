package chatweb.model.event;

import chatweb.entity.Game;
import chatweb.mapper.GameMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceGameStateChangedEvent extends RoomEvent implements PersonalEventProducer {
    private Game game;

    @Override
    public IEvent getPersonalEvent(Integer userId) {
        return new GameStateChangedEvent(
                getGame().getId(),
                GameMapper.mapState(userId, getGame())
        );
    }
}
