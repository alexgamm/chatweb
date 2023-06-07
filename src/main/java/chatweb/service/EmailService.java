package chatweb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private final String smtpHost;
    private final int smtpPort;
    private final String email;
    private final String fromName;
    private final String password;
    private Session session;

    public EmailService(String smtpHost, int smtpPort, String email, String fromName, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.email = email;
        this.fromName = fromName;
        this.password = password;
    }

    private Session getSession() {
        if (session == null) {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.socketFactory.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
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
            msg.setFrom(new InternetAddress(email, fromName));
            msg.setReplyTo(InternetAddress.parse(email, false));
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
