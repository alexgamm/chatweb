package chatweb.model.game;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder(toBuilder = true)
public record Settings(
        String dictionaryId,
        Integer turnSeconds,
        BoardSize boardSize
) {

    public static Settings getDefault(@NotNull String dictionaryId) {
        return new Settings(dictionaryId, 60, BoardSize.FIVE_X_FIVE);
    }

    public SettingsBuilder copy() {
        return toBuilder();
    }
}
