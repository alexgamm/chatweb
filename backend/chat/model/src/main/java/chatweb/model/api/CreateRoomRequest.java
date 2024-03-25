package chatweb.model.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomRequest {
    private int creatorId;
    @NotNull(message = "Invalid prefix")
    @Pattern(regexp = "^[a-z]+$", message = "Prefix can only contain lowercase letters")
    private String prefix;
    private String password;
}
