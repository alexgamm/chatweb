package chatweb.service;

import chatweb.configuration.properties.SmtpProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(SmtpProperties.class)
public class EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final SmtpProperties smtpProperties;
    private Session session;

    private Session getSession() {
        if (session == null) {
            Properties props = new Properties();
            props.put("mail.smtp.host",  smtpProperties.getHost());
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

    public boolean send(String to, String subject, String text) {
        try {
            MimeMessage msg = new MimeMessage(getSession());
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(smtpProperties.getEmail(), smtpProperties.getFromName()));
            msg.setReplyTo(InternetAddress.parse(smtpProperties.getEmail(), false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(text, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            Transport.send(msg);
            return true;
        } catch (Throwable e) {
            LOG.error("Failed sending message", e);
            return false;
        }
    }
}
