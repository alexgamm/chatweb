package chatweb.configuration;

import chatweb.email.EmailTemplate;
import chatweb.email.SmtpSender;
import chatweb.email.context.VerificationCodeContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;

@Configuration
public class EmailTemplateConfiguration {

    @Bean
    public EmailTemplate<VerificationCodeContext> verificationCodeTemplate(
            SmtpSender smtpSender,
            TemplateEngine templateEngine
    ) {
        return new EmailTemplate<>(
                smtpSender,
                templateEngine,
                "Verification code",
                "verification-code"
        );
    }
}
