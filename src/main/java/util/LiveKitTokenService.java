package util;

import org.json.JSONObject;

import modelo.entities.LlamadaTokenRespuesta;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LiveKitTokenService {

    private final HttpClient httpClient;

    public LiveKitTokenService() {
        this(HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build());
    }

    LiveKitTokenService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public LlamadaTokenRespuesta crearSalaYTokens(long sessionId, long tutorId, long studentId) {
        JSONObject body = new JSONObject();
        body.put("sessionId", String.valueOf(sessionId));
        body.put("tutorId", String.valueOf(tutorId));
        body.put("studentId", String.valueOf(studentId));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LiveKitConfig.renderCreateUrl()))
                .header("Content-Type", "application/json")
                .header("X-Internal-Key", LiveKitConfig.internalKey())
                .timeout(java.time.Duration.ofSeconds(60)) // cubre el cold start de Render free tier
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String detalle = "El servicio de Render respondió con estado " + response.statusCode()
                        + ". Body: " + response.body();
                System.err.println("DEBUG - " + detalle);
                throw new IllegalStateException(detalle);
            }

            JSONObject json = new JSONObject(response.body());
            return new LlamadaTokenRespuesta(
                    json.getString("roomName"),
                    json.getString("livekitUrl"),
                    json.getString("tutorToken"),
                    json.getString("studentToken"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La llamada al servicio de Render fue interrumpida.", e);
        } catch (IOException e) {
            System.err.println(
                    "DEBUG - IOException al conectar con Render. URL usada: " + LiveKitConfig.renderCreateUrl());
            throw new IllegalStateException("No se pudo comunicar con el servicio de Render.", e);
        }
    }
}