package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginResponse;
import chatweb.model.api.VerificationRequest;
import chatweb.repository.UserRepository;
import chatweb.service.JwtService;
import chatweb.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/verification")
@RequiredArgsConstructor
public class ApiVerificationController implements ApiControllerHelper {
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final JwtService jwtService;

    @PostMapping
    public LoginResponse verification(@RequestBody VerificationRequest body) throws ApiErrorException {
        if (body.getEmail() == null || body.getEmail().isEmpty()) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, null));
        }
        User user = userRepository.findUserByEmail(body.getEmail().toLowerCase());
        if (user == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, null));
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, null));
        }
        if (body.getCode() == null || !body.getCode().equals(verification.getCode())) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, null));
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        String accessToken = jwtService.createToken(user.getId());
        return new LoginResponse(accessToken, false);
    }
}
