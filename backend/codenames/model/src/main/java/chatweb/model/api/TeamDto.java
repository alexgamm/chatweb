package chatweb.model.api;

import chatweb.model.Color;
import chatweb.model.game.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class TeamDto {
    private Integer id;
    private Set<Member> players;
    private Color color;
    private Member leader;
}
