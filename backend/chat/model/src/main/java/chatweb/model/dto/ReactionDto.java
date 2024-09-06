package chatweb.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReactionDto {
    private final String reaction;
    private final int count;
    private final boolean hasOwn;
}
