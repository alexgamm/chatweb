package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.model.MessageDto;

public class MessageMapper {
    public static MessageDto messageToMessageDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getMessage(),
                message.getUser().getUsername(),
                message.getRepliedMessage() == null ? null : messageToMessageDto(message.getRepliedMessage()),
                message.getSendDate()
        );
    }
}
