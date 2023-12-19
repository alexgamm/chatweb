package chatweb.exception;

import chatweb.model.api.ApiError;
import lombok.Getter;

@Getter
public class ApiErrorException extends Exception{
    private final ApiError apiError;

    public ApiErrorException(ApiError apiError) {
        this.apiError = apiError;
    }
}
