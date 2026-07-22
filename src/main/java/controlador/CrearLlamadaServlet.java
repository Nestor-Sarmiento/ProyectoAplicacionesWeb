package controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.services.LlamadaService;
import util.EnvLoader;
import util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/llamadas")
public class CrearLlamadaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final LlamadaService llamadaService = new LlamadaService();

	static {
		EnvLoader.ensureLoaded();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());

		Long tutorId;
		Long studentId;
		try {
			tutorId = parseLongRequired(request.getParameter("tutorId"), "tutorId");
			studentId = parseLongRequired(request.getParameter("studentId"), "studentId");
		} catch (IllegalArgumentException e) {
			responderError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}

		LlamadaService.ResultadoLlamada resultado;
		try {
			resultado = llamadaService.crearLlamada(tutorId, studentId);
		} catch (IllegalArgumentException e) {
			responderError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		} catch (IllegalStateException e) {
			responderError(response, HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
			return;
		}

		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (PrintWriter writer = response.getWriter()) {
			writer.write("{");
			writer.write("\"sessionId\":" + resultado.sesion.getId() + ",");
			writer.write("\"roomName\":\"" + JsonUtil.escape(resultado.sesion.getRoomName()) + "\",");
			writer.write("\"livekitUrl\":\"" + JsonUtil.escape(resultado.sesion.getLivekitUrl()) + "\",");
			writer.write("\"linkTutor\":\"" + JsonUtil.escape(resultado.linkTutor) + "\",");
			writer.write("\"linkEstudiante\":\"" + JsonUtil.escape(resultado.linkEstudiante) + "\"");
			writer.write("}");
		}
	}

	private Long parseLongRequired(String value, String name) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("El parámetro '" + name + "' es obligatorio.");
		}
		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("El parámetro '" + name + "' debe ser numérico.");
		}
	}

	private void responderError(HttpServletResponse response, int status, String mensaje) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (PrintWriter writer = response.getWriter()) {
			writer.write("{\"error\":\"" + JsonUtil.escape(mensaje) + "\"}");
		}
	}
}
