package chatweb.email;

import chatweb.configuration.properties.SmtpProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
@EnableConfigurationProperties(SmtpProperties.class)
@EnableRetry
@EnableAsync
public class SmtpSender {
    private static final Logger LOG = LoggerFactory.getLogger(SmtpSender.class);

    private final SmtpProperties smtpProperties;
    private Session session;

    private Session getSession() {
        if (session == null) {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpProperties.getHost());
            props.put("mail.smtp.socketFactory.port", smtpProperties.getPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpProperties.getEmail(), smtpProperties.getPassword());
                }
            };
            session = Session.getInstance(props, authenticator);
        }
        return session;
    }

    @Async
    @Retryable(backoff = @Backoff(delayExpression = "${smtp.send.retry.backoff:30000}"))
    @SneakyThrows
    public CompletableFuture<Void> send(String to, String subject, String text) {
        MimeMessage msg = new MimeMessage(getSession());
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        msg.setFrom(new InternetAddress(smtpProperties.getEmail(), smtpProperties.getFromName()));
        msg.setReplyTo(InternetAddress.parse(smtpProperties.getEmail(), false));
        msg.setSubject(subject, "UTF-8");
        msg.setContent(text, "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        Transport.send(msg);
        return CompletableFuture.completedFuture(null);
    }
}
