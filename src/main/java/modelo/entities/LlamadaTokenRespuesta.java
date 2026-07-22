package modelo.entities;

public record LlamadaTokenRespuesta(
        String roomName,
        String livekitUrl,
        String tutorToken,
        String studentToken
) {
}