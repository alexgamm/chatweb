package chatweb.model.api;

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
    private String dictionaryId;
}
