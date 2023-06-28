package chatweb.controller;

import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.UserListResponse;
import chatweb.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserRepository userRepository;

    @GetMapping
    public UserListResponse users() {
        List<UserListResponse.User> list = userRepository.findAll().stream()
                .map(user -> new UserListResponse.User(user.getUsername(), user.getLastActivityAt()))
                .toList();
        return new UserListResponse(list);
    }

    @PatchMapping
    @Transactional
    public ResponseEntity<String> changeUsername(@RequestBody User input, HttpServletRequest request) throws ApiErrorException {
        User user = (User) request.getAttribute("user");
        if (input.getUsername().equals("")) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "invalid username"));
        }
        if (userRepository.findUserByUsername(input.getUsername()) != null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "username is already taken"));
        }
        userRepository.updateUsername(user.getId(), input.getUsername());
        return ResponseEntity.ok("");
    }

    @ExceptionHandler(ApiErrorException.class)
    public ApiError changeUsernameException(HttpServletResponse response, ApiErrorException ex){
        response.setStatus(ex.getApiError().getStatusCode().value());
        return ex.getApiError();
    }
}
