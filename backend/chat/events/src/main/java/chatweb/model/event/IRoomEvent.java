package chatweb.model.event;

public interface IRoomEvent extends IEvent {
    Integer getRoomId();

    //TODO rework this logic
    void setRoomId(Integer roomId);
}
