package chatweb.model.api;

import chatweb.model.game.BoardSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSettingsRequest {
    @Nullable
    private BoardSize boardSize;
    @Nullable
    @Range(min = 10, max = 120, message = "Turn seconds is out of acceptable range")
    private Integer turnSeconds;
    @Nullable
    private String dictionaryId;
}
