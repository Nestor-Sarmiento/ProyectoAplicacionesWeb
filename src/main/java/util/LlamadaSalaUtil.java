package util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import modelo.entities.LlamadaAcceso;
import modelo.entities.LlamadaTokenRespuesta;
import modelo.entities.SesionLlamada;

public final class LlamadaSalaUtil {

    private LlamadaSalaUtil() {
    }

    public static String construirEnlaceAcceso(String roomName, String livekitUrl, long sessionId, String token) {
        String base = LiveKitConfig.frontendBaseUrl();
        String baseNormalizada = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return baseNormalizada + "/call?room=" + codificar(roomName)
                + "&livekitUrl=" + codificar(livekitUrl)
                + "&sessionId=" + sessionId
                + "&token=" + codificar(token);
    }

    public static String enlaceTutor(SesionLlamada sesion) {
        if (sesion == null || sesion.getId() == null
                || blank(sesion.getRoomName()) || blank(sesion.getLivekitUrl())
                || blank(sesion.getTutorToken())) {
            return null;
        }
        return construirEnlaceAcceso(
                sesion.getRoomName(), sesion.getLivekitUrl(), sesion.getId(), sesion.getTutorToken());
    }

    public static String enlaceEstudiante(SesionLlamada sesion) {
        if (sesion == null || sesion.getId() == null
                || blank(sesion.getRoomName()) || blank(sesion.getLivekitUrl())
                || blank(sesion.getStudentToken())) {
            return null;
        }
        return construirEnlaceAcceso(
                sesion.getRoomName(), sesion.getLivekitUrl(), sesion.getId(), sesion.getStudentToken());
    }

    public static LlamadaAcceso crearAcceso(String rol, String correo, LlamadaTokenRespuesta respuesta,
                                            long sessionId, String token) {
        return new LlamadaAcceso(
                rol,
                correo,
                construirEnlaceAcceso(respuesta.roomName(), respuesta.livekitUrl(), sessionId, token)
        );
    }

    public static Optional<Long> extraerSessionId(String pathInfo) {
        if (pathInfo == null || pathInfo.isBlank()) {
            return Optional.empty();
        }
        String[] segmentos = pathInfo.split("/");
        for (String segmento : segmentos) {
            if (segmento == null || segmento.isBlank() || "rating".equalsIgnoreCase(segmento)) {
                continue;
            }
            if (esNumero(segmento)) {
                return Optional.of(Long.parseLong(segmento));
            }
        }
        return Optional.empty();
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor == null ? "" : valor, StandardCharsets.UTF_8);
    }

    private static boolean blank(String valor) {
        return valor == null || valor.isBlank();
    }

    private static boolean esNumero(String valor) {
        for (int i = 0; i < valor.length(); i++) {
            if (!Character.isDigit(valor.charAt(i))) {
                return false;
            }
        }
        return !valor.isBlank();
    }
}