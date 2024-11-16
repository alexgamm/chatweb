package chatweb.controller;

import chatweb.controller.helper.AuthControllerHelper;
import chatweb.email.EmailTemplate;
import chatweb.email.context.ResetPasswordContext;
import chatweb.entity.ResetPasswordSession;
import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.exception.ApiErrorException;
import chatweb.model.api.EmptyResponse;
import chatweb.model.api.LoginResponse;
import chatweb.model.api.ResetPasswordRequest;
import chatweb.model.api.SendResetPasswordRequest;
import chatweb.repository.ResetPasswordSessionRepository;
import chatweb.service.JwtService;
import chatweb.service.UserService;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static chatweb.model.api.ApiError.badRequest;

@RestController
@RequestMapping("api/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController implements ApiControllerHelper, AuthControllerHelper {
    private final UserService userService;
    private final ResetPasswordSessionRepository resetPasswordSessionRepository;
    private final VerificationService verificationService;
    @Getter
    private final JwtService jwtService;
    private final EmailTemplate<ResetPasswordContext> resetPasswordTemplate;
    @Value("${app.url}")
    private String appUrl;
    @Value("${reset-password.session.ttl:PT1H}")
    private Duration sessionTtl;

    @PostMapping("send")
    public ResponseEntity<EmptyResponse> sendResetUrl(
            @RequestBody SendResetPasswordRequest body
    ) throws ApiErrorException {
        String email = body.getEmail();
        if (email == null || email.isEmpty()) {
            throw badRequest("Missing email").toException();
        }
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.ok(new EmptyResponse());
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null || !verification.isVerified()) {
            return ResponseEntity.ok(new EmptyResponse());
        }
        String sessionId = UUID.randomUUID().toString();
        String uri = UriComponentsBuilder
                .fromUriString(appUrl)
                .path("/reset-password/{sessionId}")
                .build(Map.of("sessionId", sessionId))
                .toString();
        resetPasswordTemplate.send(
                email,
                new ResetPasswordContext(user.getUsername(), uri)
        );
        resetPasswordSessionRepository.save(
                new ResetPasswordSession()
                        .setId(sessionId)
                        .setEmail(email)
                        .setTtlSeconds(sessionTtl.toSeconds())
        );
        return ResponseEntity.ok(new EmptyResponse());
    }

    @PostMapping
    public LoginResponse resetPassword(@RequestBody ResetPasswordRequest body) throws ApiErrorException {
        ResetPasswordSession session = Optional.ofNullable(body.getSessionId())
                .flatMap(resetPasswordSessionRepository::findById)
                .orElseThrow(() -> badRequest("Link for password reset is expired").toException());
        if (!PasswordUtils.validate(body.getNewPassword())) {
            throw badRequest("Password is missing or short").toException();
        }
        User user = userService.findUserByEmail(session.getEmail());
        if (user == null) {
            throw badRequest("Something went wrong").toException();
        }
        resetPasswordSessionRepository.delete(session);
        userService.updatePassword(user.getId(), body.getNewPassword());
        return auth(user.getId());
    }
}
