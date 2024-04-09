package chatweb.controller;

import chatweb.exception.ApiErrorException;
import chatweb.exception.UnauthorizedException;
import chatweb.model.api.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface ApiControllerHelper {
    @ExceptionHandler(ApiErrorException.class)
    @ResponseBody
    default ApiError handleApiError(HttpServletResponse response, ApiErrorException ex) {
        response.setStatus(ex.getApiError().getStatusCode());
        return ex.getApiError();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    default ApiError handleUnauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new ApiError(HttpStatus.UNAUTHORIZED, "unauthorized");
    }
}
