package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public final class EmailService {

	private EmailService() {
	}

	public static void enviarEnlaceLlamada(
			String destinatarioEmail,
			String nombreDestinatario,
			String rol,
			String enlace,
			String materia,
			String fecha,
			String horario) {
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
			String html = renderPlantilla(Map.of(
					"ROL", nullSafe(rol),
					"NOMBRE", nullSafe(nombreDestinatario),
					"MATERIA", nullSafe(materia),
					"FECHA", nullSafe(fecha),
					"HORARIO", nullSafe(horario),
					"ENLACE", nullSafe(enlace)));

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(LiveKitConfig.smtpFrom(), "OwlShare", StandardCharsets.UTF_8.name()));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarioEmail));
			message.setSubject("Tu sesión de tutoría OwlShare está lista", StandardCharsets.UTF_8.name());
			message.setContent(html, "text/html; charset=UTF-8");

			Transport.send(message);
		} catch (MessagingException | IOException e) {
			System.err.println("No se pudo enviar el correo a " + destinatarioEmail + ": " + e.getMessage());
			throw new IllegalStateException(
					"No se pudo enviar el correo a " + destinatarioEmail + ".", e);
		}
	}

	private static String renderPlantilla(Map<String, String> valores) throws IOException {
		String plantilla;
		try (InputStream in = EmailService.class.getClassLoader()
				.getResourceAsStream("email/enlace-tutoria.html")) {
			if (in == null) {
				throw new IOException("No se encontró email/enlace-tutoria.html en el classpath.");
			}
			plantilla = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
		for (Map.Entry<String, String> entry : valores.entrySet()) {
			plantilla = plantilla.replace("{{" + entry.getKey() + "}}", escapeHtml(entry.getValue()));
		}
		return plantilla;
	}

	private static String nullSafe(String value) {
		return value == null ? "" : value;
	}

	private static String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;");
	}
}
