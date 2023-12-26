package chatweb.model.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;

@JsonSerialize
@AllArgsConstructor
public class ApiResponse {
    private final boolean success;
}
