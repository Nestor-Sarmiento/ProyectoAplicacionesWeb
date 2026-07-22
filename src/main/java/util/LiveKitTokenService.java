package util;

import modelo.entities.LlamadaTokenRespuesta;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiveKitTokenService {

	private final HttpClient httpClient;

	public LiveKitTokenService() {
		this(HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(30))
				.build());
	}

	LiveKitTokenService(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public LlamadaTokenRespuesta crearSalaYTokens(long sessionId, long tutorId, long studentId) {
		String url = LiveKitConfig.renderCreateUrl();
		String body = "{\"sessionId\":\"" + sessionId
				+ "\",\"tutorId\":\"" + tutorId
				+ "\",\"studentId\":\"" + studentId + "\"}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.header("X-Internal-Key", LiveKitConfig.internalKey())
				.timeout(Duration.ofSeconds(90))
				.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
				.build();

		IOException lastIo = null;
		for (int intento = 1; intento <= 2; intento++) {
			try {
				HttpResponse<String> response = httpClient.send(request,
						HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
				String responseBody = response.body() == null ? "" : response.body();

				if (response.statusCode() < 200 || response.statusCode() >= 300) {
					String detalle = "Render HTTP " + response.statusCode() + ": " + acortar(responseBody, 300);
					System.err.println("DEBUG LiveKitTokenService - " + detalle + " | URL=" + url);
					throw new IllegalStateException(detalle);
				}

				return new LlamadaTokenRespuesta(
						campoRequerido(responseBody, "roomName"),
						campoRequerido(responseBody, "livekitUrl"),
						campoRequerido(responseBody, "tutorToken"),
						campoRequerido(responseBody, "studentToken"));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("La llamada al servicio de Render fue interrumpida.", e);
			} catch (IOException e) {
				lastIo = e;
				System.err.println("DEBUG LiveKitTokenService - IO intento " + intento
						+ " URL=" + url + " error=" + e.getMessage());
				if (intento == 1) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new IllegalStateException("La llamada al servicio de Render fue interrumpida.", ie);
					}
				}
			}
		}

		throw new IllegalStateException(
				"No se pudo comunicar con Render (" + url + "): "
						+ (lastIo != null ? lastIo.getMessage() : "error de red"),
				lastIo);
	}

	private static String campoRequerido(String json, String nombre) {
		Pattern pattern = Pattern.compile(
				"\"" + Pattern.quote(nombre) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
		Matcher matcher = pattern.matcher(json);
		if (!matcher.find()) {
			throw new IllegalStateException(
					"La respuesta de Render no incluye \"" + nombre + "\". Body: " + acortar(json, 300));
		}
		return desescapar(matcher.group(1));
	}

	private static String desescapar(String value) {
		return value
				.replace("\\\"", "\"")
				.replace("\\\\", "\\")
				.replace("\\/", "/")
				.replace("\\n", "\n")
				.replace("\\r", "\r")
				.replace("\\t", "\t");
	}

	private static String acortar(String value, int max) {
		if (value == null) {
			return "";
		}
		String trimmed = value.replaceAll("\\s+", " ").trim();
		return trimmed.length() <= max ? trimmed : trimmed.substring(0, max) + "...";
	}
}
