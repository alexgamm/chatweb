package chatweb.model.dto;

import chatweb.model.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDto {
    private int id;
    private String username;
    private String email;
    private boolean hasPassword;
    private Color color;
}
