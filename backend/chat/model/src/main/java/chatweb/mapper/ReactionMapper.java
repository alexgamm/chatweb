package chatweb.mapper;

import chatweb.entity.Reaction;
import chatweb.model.dto.ReactionDto;

import java.util.Set;
import java.util.stream.Collectors;

public class ReactionMapper {
    public static Set<ReactionDto> groupReactions(Set<Reaction> reactions, int userId) {
        return reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReaction))
                .entrySet().stream()
                .map(entry -> new ReactionDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().anyMatch(reaction -> reaction.getUserId() == userId)
                ))
                .collect(Collectors.toSet());
    }
}
