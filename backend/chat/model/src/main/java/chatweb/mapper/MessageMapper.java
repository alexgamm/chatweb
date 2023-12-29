package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.model.dto.MessageDto;

import java.util.Set;
import java.util.stream.Collectors;

public class MessageMapper {
    public static MessageDto messageToMessageDto(
            Message message,
            Integer userId,
            boolean includeRepliedMessage,
            boolean includeReactions
    ) {
        return new MessageDto(
                message.getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getMessage(),
                message.getKey(),
                includeRepliedMessage
                        ? messageToMessageDto(message.getRepliedMessage(), userId, false, false)
                        : null,
                message.getSendDate(),
                includeReactions ? groupReactions(message.getReactions(), userId) : null
        );
    }

    public static MessageDto messageToMessageDto(
            Message message,
            Integer userId,
            boolean includeReactions
    ) {
        return messageToMessageDto(message, userId, message.getRepliedMessage() != null, includeReactions);
    }

    public static Set<MessageDto.Reaction> groupReactions(Set<Reaction> reactions, int userId) {
        return reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReaction))
                .entrySet().stream()
                .map(entry -> new MessageDto.Reaction(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().anyMatch(reaction -> reaction.getUserId() == userId)
                ))
                .collect(Collectors.toSet());
    }
}
