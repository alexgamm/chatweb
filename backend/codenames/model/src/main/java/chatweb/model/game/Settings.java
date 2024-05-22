package chatweb.model.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class Settings {
    private String dictionaryId;
    private Integer turnSeconds;
    private BoardSize boardSize;

    public static Settings getDefault(@NotNull String dictionaryId) {
        return new Settings(dictionaryId, 60, BoardSize.FIVE_X_FIVE);
    }

    public SettingsBuilder copy() {
        return toBuilder();
    }
}
