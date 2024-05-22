package chatweb.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestEvent implements IEvent {
    private Instant date;
    private String cyrillicText;
}
