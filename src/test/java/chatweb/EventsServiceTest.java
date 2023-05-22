package chatweb;

import chatweb.model.Event;
import chatweb.model.EventType;
import chatweb.model.Message;
import chatweb.model.NewMessage;
import chatweb.service.EventsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EventsServiceTest {
    private final EventsService eventsService = new EventsService();

    @Test
    public void addEventTest() {
        eventsService.addEvent(new NewMessage(new Message("dg654" ,"hi", "noname", null)));
        List<Event> events = eventsService.getEvents(0);
        Assertions.assertEquals(1, events.size());
        Event event = events.get(0);
        Assertions.assertTrue(event instanceof NewMessage);
        Assertions.assertEquals(EventType.NEW_MESSAGE, event.getType());
        Assertions.assertEquals("hi", ((NewMessage) event).getMessage().getMessage());
        Assertions.assertEquals("noname", ((NewMessage) event).getMessage().getUsername());
        List<Event> emptyEvents = eventsService.getEvents(event.getDate().getTime());
        Assertions.assertEquals(0, emptyEvents.size());
    }
}
