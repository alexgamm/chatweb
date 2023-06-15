package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.repository.UserRepository;
import chatweb.response.TemplateResponse;
import chatweb.service.GoogleOAuthService;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import com.github.jknack.handlebars.Handlebars;
import org.apache.commons.validator.routines.EmailValidator;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Date;

public class RegistrationEndpoint implements Endpoint {
    private static final String TEMPLATE_NAME = "registration";
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();
    private final Handlebars handlebars;
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final GoogleOAuthService googleOAuthService;

    public RegistrationEndpoint(Handlebars handlebars, UserRepository userRepository, VerificationService verificationService, GoogleOAuthService googleOAuthService) {
        this.handlebars = handlebars;
        this.userRepository = userRepository;
        this.verificationService = verificationService;
        this.googleOAuthService = googleOAuthService;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                .addToContext("googleOAuthClientId", googleOAuthService.getClientId())
                .addToContext("googleOAuthRedirectUri", googleOAuthService.getRedirectUri())
                .build();
    }

    @Override
    public Object post(Request request) throws RequestFailedException {
        String username = request.getBody().get("username");
        String email = request.getBody().get("email");
        String password = request.getBody().get("password");

        if (username == null || username.isEmpty()) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("error", "username is missing")
                    .build();
        }
        if (email == null || email.isEmpty() || !EMAIL_VALIDATOR.isValid(email)) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("error", "email is missing or invalid")
                    .addToContext("username", username)
                    .addToContext("email", email)
                    .build();
        }
        if (password == null || password.length() < 6) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("error", "password is missing or short")
                    .addToContext("username", username)
                    .build();
        }
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return new TemplateResponse.Builder(handlebars, TEMPLATE_NAME)
                    .statusCode(400)
                    .addToContext("username", username)
                    .addToContext("error", "username has been already taken")
                    .build();
        }
        user = new User(0, username.toLowerCase(), email, PasswordUtils.hash(password), new Date());
        userRepository.saveUser(user);
        user = userRepository.findUserByUsername(user.getUsername());
        verificationService.createAndSendVerification(user);
        //TODO handle email problems
        return Response.redirect("/verification?email=" + user.getEmail());
    }
}
