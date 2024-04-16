package chatweb.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class EndGame implements GameAction {
    private final Set<Integer> lostTeamId;
}
