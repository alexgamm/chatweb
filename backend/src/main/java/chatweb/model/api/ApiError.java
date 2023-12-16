package chatweb.model.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {
    private final HttpStatus statusCode;
    private final String message;

    public ApiError(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @JsonGetter
    public int getStatusCode() {
        return statusCode.value();
    }
}
