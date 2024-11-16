package chatweb.controller;

import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.UserMapper;
import chatweb.model.api.RegistrationRequest;
import chatweb.model.dto.UserDto;
import chatweb.service.UserService;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static chatweb.model.api.ApiError.badRequest;
import static chatweb.model.api.ApiError.conflict;

@RestController
@RequestMapping("api/registration")
@RequiredArgsConstructor
public class ApiRegistrationController implements ApiControllerHelper {
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping
    public UserDto registration(@RequestBody RegistrationRequest body) throws ApiErrorException {
        String username = body.getUsername();
        String email = body.getEmail();
        String password = body.getPassword();
        if (username == null || username.isEmpty()) {
            throw badRequest("username is missing").toException();
        }
        if (email == null || email.isEmpty() || !EMAIL_VALIDATOR.isValid(email)) {
            throw badRequest("email is missing or invalid").toException();
        }
        if (userService.existsByEmail(email)) {
            throw conflict("email is already taken").toException();
        }
        if (!PasswordUtils.validate(password)) {
            throw badRequest("password is missing or short").toException();
        }
        if (userService.existsByUsername(username)) {
            throw conflict("username has been already taken").toException();
        }
        User user = userService.createUser(username, email, password);
        verificationService.createAndSendVerification(user);
        return UserMapper.INSTANCE.toDto(user);
    }
}
