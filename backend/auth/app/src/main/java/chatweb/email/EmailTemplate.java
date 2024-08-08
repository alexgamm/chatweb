package chatweb.email;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

@RequiredArgsConstructor
public class EmailTemplate<Ctx extends IContext> {

    private final SmtpSender smtpSender;
    private final TemplateEngine templateEngine;
    private final String subject;
    private final String templateName;

    public boolean send(String to, Ctx context) {
        String content = templateEngine.process(String.format("email/%s", templateName), context);
        return smtpSender.send(to, subject, content);
    }
}
