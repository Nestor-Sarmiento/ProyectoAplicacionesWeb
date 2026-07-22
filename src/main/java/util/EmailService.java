package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public final class EmailService {

    private EmailService() {
    }

    public static void enviarEnlaceLlamada(String destinatarioEmail, String link, String rol) {
        if (destinatarioEmail == null || destinatarioEmail.isBlank()) {
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", LiveKitConfig.smtpHost());
        props.put("mail.smtp.port", LiveKitConfig.smtpPort());

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(LiveKitConfig.smtpUser(), LiveKitConfig.smtpPassword());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(LiveKitConfig.smtpFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarioEmail));
            message.setSubject("Tu sesión de tutoría está lista");
            message.setText(
                    "Hola,\n\n" +
                            "Tu sesión como " + rol + " está lista. Ingresa con el siguiente enlace:\n\n" +
                            link + "\n\n" +
                            "El enlace es personal, no lo compartas."
            );

            Transport.send(message);
        } catch (MessagingException e) {
            System.err.println("No se pudo enviar el correo a " + destinatarioEmail + ": " + e.getMessage());
        }
    }
}