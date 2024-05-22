package chatweb.model.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest {
    @Nullable
    private String password;
    @NotNull(message = "Dictionary id is missing")
    private String dictionaryId;
}
