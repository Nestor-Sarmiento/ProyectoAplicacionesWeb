package modelo.services;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CrearLlamadaServlet extends HttpServlet {

    private final LlamadaService llamadaService = new LlamadaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToView(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        request.setAttribute("sesionLlamada", resultado.sesion);
        request.setAttribute("linkTutor", resultado.linkTutor);
        request.setAttribute("linkEstudiante", resultado.linkEstudiante);

        if (responderJson(request)) {
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
            return;
        }

        forwardToView(request, response);
    }

    private void forwardToView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/llamadas/crear-llamada.jsp");
        dispatcher.forward(request, response);
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

    private boolean responderJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String formato = request.getParameter("format");
        return (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(formato);
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