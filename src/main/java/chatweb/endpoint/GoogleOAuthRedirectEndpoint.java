package chatweb.endpoint;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.model.google.UserInfo;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.GoogleOAuthService;
import chatweb.service.VerificationService;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.io.IOException;
import java.util.Date;

public class GoogleOAuthRedirectEndpoint extends AuthEndpoint {
    private final GoogleOAuthService googleOAuthService;
    private final VerificationService verificationService;

    public GoogleOAuthRedirectEndpoint(UserRepository userRepository, SessionRepository sessionRepository, GoogleOAuthService googleOAuthService, VerificationService verificationService) {
        super(userRepository, sessionRepository);
        this.googleOAuthService = googleOAuthService;
        this.verificationService = verificationService;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        String code = request.getQuery().get("code");
        if (code == null || code.isEmpty()) {
            return Response.badRequest("something went wrong");
        }
        UserInfo userInfo;
        try {
            String token = googleOAuthService.getToken(code);
            userInfo = googleOAuthService.getUserInfo(token);
        } catch (IOException e) {
            throw new RequestFailedException(500, "internal error");
        }
        User user = userRepository.findUserByEmail(userInfo.getEmail());
        if (user == null) {
            String username = userInfo.getEmail().split("@")[0];
            user = new User(0, username, userInfo.getEmail(), null, new Date());
            userRepository.saveUser(user);
            user = userRepository.findUserByUsername(user.getUsername());
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            verificationService.createVerification(user.getId(), "");
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        return authorizeAndRedirect(user.getId(), "/");
    }
}
