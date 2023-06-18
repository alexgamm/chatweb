package chatweb.controller;

import chatweb.entity.User;
import chatweb.repository.UserRepository;
import chatweb.service.GoogleOAuthService;
import chatweb.service.VerificationService;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("registration")
@RequiredArgsConstructor
public class RegistrationController {
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final VerificationService verificationService;

    @GetMapping
    public String registration(Model model) {
        model.addAttribute("googleOAuthClientId", googleOAuthService.getClientId());
        model.addAttribute("googleOAuthRedirectUri", googleOAuthService.getRedirectUri());
        return "registration";
    }

    @PostMapping
    public String registration(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        model.addAttribute("googleOAuthClientId", googleOAuthService.getClientId());
        model.addAttribute("googleOAuthRedirectUri", googleOAuthService.getRedirectUri());
        if (username == null || username.isEmpty()) {
            model.addAttribute("error", "username is missing");
            return "registration";
        }
        if (email == null || email.isEmpty() || !EMAIL_VALIDATOR.isValid(email)) {
            model.addAttribute("error", "email is missing or invalid");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "registration";
        }
        if (password == null || password.length() < 6) {
            model.addAttribute("error", "password is missing or short");
            model.addAttribute("username", username);
            return "registration";
        }
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            // TODO check email too
            model.addAttribute("error", "username has been already taken");
            model.addAttribute("username", username);
            return "registration";
        }
        user = new User(0, username.toLowerCase(), email, PasswordUtils.hash(password), new Date());
        userRepository.saveUser(user);
        user = userRepository.findUserByUsername(user.getUsername());
        verificationService.createAndSendVerification(user);
        //TODO handle email problems
        return "redirect:/verification?email=" + user.getEmail();
    }
}

