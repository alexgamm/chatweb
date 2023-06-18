package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("login")
public class LoginController extends AuthController {
    private final UserRepository userRepository;
    private final VerificationService verificationService;

    public LoginController(SessionRepository sessionRepository, UserRepository userRepository, VerificationService verificationService) {
        super(sessionRepository);
        this.userRepository = userRepository;
        this.verificationService = verificationService;
    }

    @GetMapping
    public String login() {
        return "login";
    }

    @PostMapping
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpServletResponse response) {
        User user = userRepository.findUserByUsername(username);
        if (user == null || password == null || !PasswordUtils.check(password, user.getPassword())) {
            model.addAttribute("username", username);
            model.addAttribute("error", true);
            return "login"; //TODO set status code
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            verificationService.createAndSendVerification(user);
            verification = verificationService.findVerification(user.getId());
        }
        if (!verification.isVerified()) {
            return "redirect:/verification?email=" + user.getEmail();
        }
        return authorizeAndRedirect(response, user.getId(), "/");
    }
}
