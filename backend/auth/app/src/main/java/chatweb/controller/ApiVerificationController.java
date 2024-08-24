package chatweb.controller;

import chatweb.controller.helper.AuthControllerHelper;
import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.LoginResponse;
import chatweb.model.api.VerificationRequest;
import chatweb.service.JwtService;
import chatweb.service.UserService;
import chatweb.service.VerificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/verification")
@RequiredArgsConstructor
public class ApiVerificationController implements ApiControllerHelper, AuthControllerHelper {
    private final UserService userService;
    private final VerificationService verificationService;
    @Getter
    private final JwtService jwtService;

    @PostMapping
    public LoginResponse verification(@RequestBody VerificationRequest body) throws ApiErrorException {
        if (body.getEmail() == null || body.getEmail().isEmpty()) {
            throw new ApiErrorException(new ApiError(HttpStatus.BAD_REQUEST, null));
        }
        User user = userService.findUserByEmail(body.getEmail());
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
        return auth(user.getId());
    }
}
