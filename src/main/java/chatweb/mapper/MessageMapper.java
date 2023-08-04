package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.model.dto.MessageDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageMapper {
    public static MessageDto messageToMessageDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getMessage(),
                message.getRepliedMessage() == null ? null : messageToMessageDto(message.getRepliedMessage()),
                message.getSendDate(),
                groupReactions(message.getReactions())
        );
    }

    private static Set<MessageDto.Reaction> groupReactions(Set<Reaction> reactions) {
        return reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReaction))
                .entrySet().stream()
                .map(entry -> new MessageDto.Reaction(entry.getKey(), entry.getValue().size()))
                .collect(Collectors.toSet());
    }
}
