package chatweb.model.game.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
@AllArgsConstructor
@Getter
public class Card {
    private CardType type;
    private String word;
    @Nullable
    private Integer teamId;
    @Nullable
    private Integer pickedByTeamId;
}
