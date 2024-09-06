package chatweb.model.api;

import chatweb.model.game.Member;
import chatweb.model.game.Settings;

import java.util.List;
import java.util.Set;

public record GameDto(
        String id,
        Member host,
        Set<Member> viewers,
        List<TeamDto> teams,
        Settings settings
) {
}
