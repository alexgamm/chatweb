package chatweb.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PickCard implements GameAction {
    private final String word;
    private final Integer pickedTeamId;
    private final Integer turnSeconds;
}
