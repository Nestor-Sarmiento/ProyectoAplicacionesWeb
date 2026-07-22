package util;

public final class LiveKitConfig {

    private LiveKitConfig() {
    }

    public static String renderCreateUrl() {
        return requiredEnv("CALLS_RENDER_URL", "URL del servicio Render que crea la sala y los tokens");
    }

    public static String internalKey() {
        return requiredEnv("CALLS_INTERNAL_KEY", "clave interna para llamar al servicio de Render");
    }

    public static String frontendBaseUrl() {
        return envOrDefault("CALLS_FRONTEND_BASE_URL", "http://localhost:3000");
    }

    public static String allowedOrigin() {
        return envOrDefault("CALLS_ALLOWED_ORIGIN", frontendBaseUrl());
    }

    public static String smtpHost() {
        return requiredEnv("SMTP_HOST", "host SMTP");
    }

    public static String smtpPort() {
        return envOrDefault("SMTP_PORT", "587");
    }

    public static String smtpUser() {
        return requiredEnv("SMTP_USER", "usuario SMTP");
    }

    public static String smtpPassword() {
        return requiredEnv("SMTP_PASSWORD", "contraseña SMTP");
    }

    public static String smtpFrom() {
        return envOrDefault("SMTP_FROM", smtpUser());
    }

    private static String requiredEnv(String name, String description) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta configurar " + description + " en la variable de entorno " + name + ".");
        }
        return value.trim();
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}