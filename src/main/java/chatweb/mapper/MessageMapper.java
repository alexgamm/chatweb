package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.model.dto.MessageDto;

public class MessageMapper {
    public static MessageDto messageToMessageDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getMessage(),
                message.getRepliedMessage() == null ? null : messageToMessageDto(message.getRepliedMessage()),
                message.getSendDate()
        );
    }
}
