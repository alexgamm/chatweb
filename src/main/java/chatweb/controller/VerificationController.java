package chatweb.controller;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.SessionRepository;
import chatweb.repository.UserRepository;
import chatweb.service.VerificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("verification")
public class VerificationController extends AuthController {
    private final UserRepository userRepository;
    private final VerificationService verificationService;

    public VerificationController(SessionRepository sessionRepository, UserRepository userRepository, VerificationService verificationService) {
        super(sessionRepository);
        this.userRepository = userRepository;
        this.verificationService = verificationService;
    }

    @GetMapping
    public String verification(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verification";
    }

    @PostMapping
    public String verification(@RequestParam String code, @RequestParam String email, Model model, HttpServletResponse response) {
        model.addAttribute("error", "something went wrong");
        model.addAttribute("email", email);
        User user = userRepository.findUserByEmail(email.toLowerCase());
        if (user == null) {
            return "verification";
        }
        Verification verification = verificationService.findVerification(user.getId());
        if (verification == null) {
            return "verification";
        }
        if (!code.equals(verification.getCode())) {
            return "verification";
        }
        if (!verification.isVerified()) {
            verificationService.updateVerified(user.getId());
        }
        return authorizeAndRedirect(response, user.getId(), "/");
    }
}

