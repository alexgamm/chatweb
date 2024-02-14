package chatweb.model.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonSerialize
@AllArgsConstructor
@Getter
public class ApiResponse {
    private final boolean success;
}
