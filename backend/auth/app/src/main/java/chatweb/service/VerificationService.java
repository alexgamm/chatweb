package chatweb.service;

import chatweb.email.EmailTemplate;
import chatweb.email.context.VerificationCodeContext;
import chatweb.entity.User;
import chatweb.entity.Verification;
import chatweb.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final EmailTemplate<VerificationCodeContext> verificationCodeTemplate;

    public boolean createAndSendVerification(User user) {
        String code = RandomStringUtils.random(6, false, true);
        boolean sent = verificationCodeTemplate.send(
                user.getEmail().toLowerCase(),
                new VerificationCodeContext(user.getUsername(), code)
        );
        if (sent) {
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
