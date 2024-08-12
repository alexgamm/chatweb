package chatweb.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

@RequiredArgsConstructor
@Slf4j
public class EmailTemplate<Ctx extends IContext> {

    private final SmtpSender smtpSender;
    private final TemplateEngine templateEngine;
    private final String subject;
    private final String templateName;

    public void send(String to, Ctx context) {
        String content = templateEngine.process(String.format("email/%s", templateName), context);
        smtpSender.send(to, subject, content).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Could not send email template {} to {}", templateName, to, ex.getCause());
            }
        });
    }
}
