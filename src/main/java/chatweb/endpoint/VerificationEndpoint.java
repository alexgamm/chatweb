package chatweb.endpoint;

import chatweb.entity.Session;
import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import chatweb.service.VerificationService;
import com.github.jknack.handlebars.Handlebars;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Collections;
import java.util.UUID;

public class VerificationEndpoint implements Endpoint {
    private final VerificationService verificationService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final Handlebars handlebars;

    public VerificationEndpoint(VerificationService verificationService, UserRepository userRepository, SessionRepository sessionRepository, Handlebars handlebars) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.handlebars = handlebars;
    }

    public Object get(Request request) throws RequestFailedException {
        String email = request.getQuery().get("email");
        return new TemplateResponse.Builder(handlebars, "verification").addToContext("email", email).build();
    }

    @Override
    public Object post(Request request) throws RequestFailedException {
        String code = request.getBody().get("code");
        String email = request.getBody().get("email");
        if (code == null || code.isEmpty()) {
            return Response.badRequest("");
        }
        if (email == null || email.isEmpty()) {
            return Response.badRequest("");
        }
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return new TemplateResponse.Builder(handlebars, "verification")
                    .statusCode(400)
                    .addToContext("error", "something went wrong")
                    .addToContext("email", email)
                    .build();
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            return new TemplateResponse.Builder(handlebars, "verification")
                    .statusCode(400)
                    .addToContext("error", "something went wrong")
                    .addToContext("email", email)
                    .build();
        }
        if (!code.equals(verification.getCode())) {
            return new TemplateResponse.Builder(handlebars, "verification")
                    .statusCode(400)
                    .addToContext("error", "invalid verification code")
                    .addToContext("email", email)
                    .build();
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        Session session = new Session(UUID.randomUUID().toString(), user.getId());
        sessionRepository.saveSession(session);
        return Response.redirect(
                "/",
                Collections.singletonMap("Set-Cookie", "sessionId=" + session.getId())
        );
    }
}
