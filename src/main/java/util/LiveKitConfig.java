package util;

public final class LiveKitConfig {

	private LiveKitConfig() {
	}

	public static String renderCreateUrl() {
		return required("CALLS_RENDER_URL", "URL del servicio que crea la sala y los tokens");
	}

	public static String internalKey() {
		return required("CALLS_INTERNAL_KEY", "clave interna para el servicio de llamadas");
	}

	public static String frontendBaseUrl() {
		String value = EnvLoader.get("CALLS_FRONTEND_BASE_URL");
		if (value == null || value.isBlank()) {
			// Typo histórico en .env del proyecto
			value = EnvLoader.get("CALLS_FRONTEND_BASE_RUL");
		}
		if (value == null || value.isBlank()) {
			value = EnvLoader.get("CALL_ALLOWED_ORIGIN");
		}
		return value == null || value.isBlank() ? "http://localhost:3000" : value.trim();
	}

	public static String allowedOrigin() {
		String value = EnvLoader.get("CALLS_ALLOWED_ORIGIN");
		if (value == null || value.isBlank()) {
			value = EnvLoader.get("CALL_ALLOWED_ORIGIN");
		}
		return value == null || value.isBlank() ? frontendBaseUrl() : value.trim();
	}

	public static String smtpHost() {
		return required("SMTP_HOST", "host SMTP");
	}

	public static String smtpPort() {
		return EnvLoader.getOrDefault("SMTP_PORT", "587");
	}

	public static String smtpUser() {
		return required("SMTP_USER", "usuario SMTP");
	}

	public static String smtpPassword() {
		return required("SMTP_PASSWORD", "contraseña SMTP");
	}

	public static String smtpFrom() {
		return EnvLoader.getOrDefault("SMTP_FROM", smtpUser());
	}

	private static String required(String name, String description) {
		String value = EnvLoader.get(name);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException(
					"Falta configurar " + description + " (" + name + ") en .env o variables de entorno.");
		}
		return value.trim();
	}
}
