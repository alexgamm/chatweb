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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("google/oauth/redirect")
public class GoogleOAuthRedirectController extends AuthController {
    private final VerificationService verificationService;
    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;

    public GoogleOAuthRedirectController(SessionRepository sessionRepository, VerificationService verificationService, GoogleOAuthService googleOAuthService, UserRepository userRepository) {
        super(sessionRepository);
        this.verificationService = verificationService;
        this.googleOAuthService = googleOAuthService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String googleOAuthRedirect(HttpServletResponse response, @RequestParam String code, Model model) throws IOException {
        model.addAttribute("googleOAuthClientId", googleOAuthService.getClientId());
        model.addAttribute("googleOAuthRedirectUri", googleOAuthService.getRedirectUri());

        UserInfo userInfo;
        String token = googleOAuthService.getToken(code);
        userInfo = googleOAuthService.getUserInfo(token);
        User user = userRepository.findUserByEmail(userInfo.getEmail().toLowerCase());
        if (user == null) {
            String username = userInfo.getEmail().split("@")[0];
            user = new User(0, username, userInfo.getEmail().toLowerCase(), null, new Date());
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
        return authorizeAndRedirect(response, user.getId(), "/");
    }
}

