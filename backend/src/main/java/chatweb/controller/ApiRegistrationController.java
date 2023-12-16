package chatweb.controller;

import chatweb.entity.User;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.UserMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.RegistrationRequest;
import chatweb.model.dto.UserDto;
import chatweb.repository.UserRepository;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import chatweb.utils.UserColorUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("api/registration")
@RequiredArgsConstructor
public class ApiRegistrationController implements ApiControllerHelper {
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private final UserRepository userRepository;
    private final VerificationService verificationService;

    @PostMapping
    public UserDto registration(@RequestBody RegistrationRequest body) throws ApiErrorException {
        String username = body.getUsername();
        String email = body.getEmail();
        String password = body.getPassword();
        if (username == null || username.isEmpty()) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "username is missing"));
        }
        if (email == null || email.isEmpty() || !EMAIL_VALIDATOR.isValid(email)) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "email is missing or invalid"));
        }
        if (userRepository.existsByEmail(email)) {
            throw new ApiErrorException(new ApiError(HttpStatus.CONFLICT, "email is already taken"));
        }
        if (!PasswordUtils.validate(password)) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, "password is missing or short"));
        }
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            throw new ApiErrorException(new ApiError(HttpStatus.CONFLICT, "username has been already taken"));
        }
        user = new User(
                null,
                username.toLowerCase(),
                email.toLowerCase(),
                PasswordUtils.hash(password),
                new Date(),
                UserColorUtils.getRandomColor()
        );
        user = userRepository.save(user);
        verificationService.createAndSendVerification(user);
        //TODO handle email problems (if email was not sent)
        return UserMapper.userToUserDto(user);
    }
}
