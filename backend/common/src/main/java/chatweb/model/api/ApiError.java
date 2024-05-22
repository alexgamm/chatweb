package chatweb.model.api;

import chatweb.exception.ApiErrorException;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonSerialize
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

    public ApiErrorException toException() {
        return new ApiErrorException(this);
    }

    public static ApiError badRequest(String message) {
        return new ApiError(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiError notFound(String message) {
        return new ApiError(HttpStatus.NOT_FOUND, message);
    }
    public static ApiError forbidden(String message) {
        return new ApiError(HttpStatus.FORBIDDEN, message);
    }
}
