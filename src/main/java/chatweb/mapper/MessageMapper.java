package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.entity.Reaction;
import chatweb.entity.User;
import chatweb.model.dto.MessageDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageMapper {
    public static MessageDto messageToMessageDto(Message message, User user) {
        return new MessageDto(
                message.getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getMessage(),
                user == null || message.getRepliedMessage() == null
                        ? null
                        : messageToMessageDto(message.getRepliedMessage(), null),
                message.getSendDate(),
                // we don't need reactions if user == null (for replied message mapping recursion)
                user == null ? null : groupReactions(message.getReactions(), user)
        );
    }

    private static Set<MessageDto.Reaction> groupReactions(Set<Reaction> reactions, User user) {
        return reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReaction))
                .entrySet().stream()
                .map(entry -> new MessageDto.Reaction(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().anyMatch(reaction -> reaction.getUserId() == user.getId())
                ))
                .collect(Collectors.toSet());
    }
}
