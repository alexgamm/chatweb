package chatweb.model.event;

public interface PersonalEventProducer {
    IEvent getPersonalEvent(Integer userId);
}
