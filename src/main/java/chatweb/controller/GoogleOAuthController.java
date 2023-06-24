package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.model.google.UserInfo;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.GoogleOAuthService;
import chatweb.service.VerificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("google/oauth")
public class GoogleOAuthController extends AuthController {
    private final VerificationService verificationService;
    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;

    public GoogleOAuthController(SessionRepository sessionRepository, VerificationService verificationService, GoogleOAuthService googleOAuthService, UserRepository userRepository) {
        super(sessionRepository);
        this.verificationService = verificationService;
        this.googleOAuthService = googleOAuthService;
        this.userRepository = userRepository;
    }

    @GetMapping("redirect")
    public String redirect(HttpServletResponse response, @RequestParam String code) {
        UserInfo userInfo;
        try {
            String token = googleOAuthService.getToken(code);
            userInfo = googleOAuthService.getUserInfo(token);
        } catch (IOException e) {
            return "redirect:/login#oauth-failed";
        }
        User user = userRepository.findUserByEmail(userInfo.getEmail().toLowerCase());
        if (user == null) {
            String username = userInfo.getEmail().split("@")[0];
            user = new User(null, username, userInfo.getEmail().toLowerCase(), null, new Date());
            user = userRepository.save(user);
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            //TODO return newly created verification
            verificationService.createVerification(user.getId(), "");
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        return authorizeAndRedirect(response, user.getId(), "/");
    }
}

