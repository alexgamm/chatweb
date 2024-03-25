package chatweb.model.game.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class Card {
    private CardType type;
    private String word;
    @Nullable
    private Integer teamId;
    @Nullable
    @Setter
    private Integer pickedByTeamId;
}
