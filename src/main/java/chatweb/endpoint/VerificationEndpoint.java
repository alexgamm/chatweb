package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import chatweb.service.VerificationService;
import com.github.jknack.handlebars.Handlebars;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

public class VerificationEndpoint extends AuthEndpoint {
    private final VerificationService verificationService;
    private final Handlebars handlebars;

    public VerificationEndpoint(UserRepository userRepository, SessionRepository sessionRepository, VerificationService verificationService, Handlebars handlebars) {
        super(userRepository, sessionRepository);
        this.verificationService = verificationService;
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
        return authorizeAndRedirect(user.getId(), "/");
    }
}
