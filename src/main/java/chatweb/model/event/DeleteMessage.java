package chatweb.model.event;

import chatweb.entity.Message;
import lombok.Getter;

import java.util.Date;

@Getter
public class DeleteMessage extends Event{
    private  final Message messageForDelete;

    public DeleteMessage(Message messageForDelete) {
        super(EventType.DELETE_MESSAGE, new Date());
        this.messageForDelete = messageForDelete;
    }
}
