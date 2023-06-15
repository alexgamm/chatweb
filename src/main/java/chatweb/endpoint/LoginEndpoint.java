package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import com.github.jknack.handlebars.Handlebars;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

public class LoginEndpoint extends AuthEndpoint {
    private final Handlebars handlebars;
    private final VerificationService verificationService;

    public LoginEndpoint(UserRepository userRepository, SessionRepository sessionRepository, Handlebars handlebars, VerificationService verificationService) {
        super(userRepository, sessionRepository);
        this.handlebars = handlebars;
        this.verificationService = verificationService;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        return new TemplateResponse.Builder(handlebars, "login").build();
    }


    @Override
    public Object post(Request request) throws RequestFailedException {
        String username = request.getBody().get("username");
        String password = request.getBody().get("password");
        User user = userRepository.findUserByUsername(username);
        if (user == null || password == null || !PasswordUtils.check(password, user.getPassword())) {
            return new TemplateResponse.Builder(handlebars, "login")
                    .statusCode(400)
                    .addToContext("username", username)
                    .addToContext("error", true).build();
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            verificationService.createAndSendVerification(user);
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            return Response.redirect("/verification?email=" + user.getEmail());
        }
        return authorizeAndRedirect(user.getId(), "/");
    }

}