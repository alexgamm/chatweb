package chatweb.model.event;

import chatweb.entity.Game;
import chatweb.mapper.GameMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceGameUpdatedEvent extends RoomEvent implements PersonalEventProducer {
    private Game game;

    @Override
    public IEvent getPersonalEvent(Integer userId) {
        return new GameUpdatedEvent(
                GameMapper.INSTANCE.map(userId, getGame())
        );
    }
}
