package chatweb.service;

import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.VerificationRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationService {
    private final EmailService emailService;
    private final VerificationRepository verificationRepository;

    public VerificationService(EmailService emailService, VerificationRepository verificationRepository) {
        this.emailService = emailService;
        this.verificationRepository = verificationRepository;
    }

    public boolean createAndSendVerification(User user) {
        String code = RandomStringUtils.random(6, false, true);
        if (emailService.send(user.getEmail().toLowerCase(), "Verification code", code)) {
            createVerification(user.getId(), code);
            return true;
        }
        return false;
    }

    public Verification findVerification(int userId) {
        return verificationRepository.findByUserId(userId);
    }

    @Transactional
    public void updateVerified(int userId) {
        verificationRepository.updateVerified(userId);
    }

    public Verification createVerification(int userId, String code) {
        return verificationRepository.save(new Verification(userId, code, false));
    }
}
