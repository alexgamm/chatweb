package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.entity.User;
import chatweb.model.dto.MessageDto;

import java.util.Set;
import java.util.stream.Collectors;

public class MessageMapper {
    public static MessageDto messageToMessageDto(
            Message message,
            User user,
            boolean includeRepliedMessage,
            boolean includeReactions
    ) {
        return new MessageDto(
                message.getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getMessage(),
                includeRepliedMessage
                        ? messageToMessageDto(message.getRepliedMessage(), user, false, false)
                        : null,
                message.getSendDate(),
                includeReactions ? groupReactions(message.getReactions(), user.getId()) : null
        );
    }

    public static MessageDto messageToMessageDto(
            Message message,
            User user,
            boolean includeReactions
    ) {
        return messageToMessageDto(message, user, message.getRepliedMessage() != null, includeReactions);
    }

    public static Set<MessageDto.Reaction> groupReactions(Set<Reaction> reactions, int userId) {
        return reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReaction))
                .entrySet().stream()
                .map(entry -> new MessageDto.Reaction(
                        //TODO
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().anyMatch(reaction -> reaction.getUserId() == userId)
                ))
                .collect(Collectors.toSet());
    }
}
