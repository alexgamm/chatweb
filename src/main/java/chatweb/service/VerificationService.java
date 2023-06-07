package chatweb.service;

import chatweb.entity.User;
import chatweb.repository.VerificationRepository;
import org.apache.commons.lang3.RandomStringUtils;

public class VerificationService {
    private final EmailService emailService;
    private final VerificationRepository verificationRepository;

    public VerificationService(EmailService emailService, VerificationRepository verificationRepository) {
        this.emailService = emailService;
        this.verificationRepository = verificationRepository;
    }

    public boolean createAndSendVerification(User user) {
        String code = RandomStringUtils.random(6, false, true);
        if (emailService.send(user.getEmail(), "Verification code", code)) {
            verificationRepository.createVerification(user.getId(), code);
            return true;
        }
        return false;
    }
}
