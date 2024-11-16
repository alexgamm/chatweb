package chatweb.model.api;

import chatweb.model.Color;
import chatweb.model.game.Member;

import java.util.Set;

public record TeamDto(
        Integer id,
        Set<Member> players,
        Color color,
        Member leader
) {
}
