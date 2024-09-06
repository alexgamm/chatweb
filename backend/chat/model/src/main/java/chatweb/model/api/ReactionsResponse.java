package chatweb.model.api;

import chatweb.model.dto.ReactionDto;

import java.util.Set;

public record ReactionsResponse(
        Set<ReactionDto> reactions
) {
}
