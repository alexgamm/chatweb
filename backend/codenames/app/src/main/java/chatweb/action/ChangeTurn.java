package chatweb.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ChangeTurn implements GameAction {
    private final Integer turnSeconds;
}
