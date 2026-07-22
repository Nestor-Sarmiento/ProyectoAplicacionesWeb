package controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.DisponibilidadDAO;
import modelo.entities.Tutor;
import modelo.entities.Usuario;

@WebServlet("/tutor")
public class TutorController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ruteador(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ruteador(req, resp);
	}

	private void ruteador(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String ruta = req.getParameter("ruta");

		if (ruta == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta no especificada");
			return;
		}

		switch (ruta) {
			case "inicio" -> inicio(req, resp);
			case "horarios" -> horarios(req, resp);
			case "guardar-horarios" -> guardarHorarios(req, resp);
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
		}
	}

	private void inicio(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Tutor tutor = requerirTutor(req, resp);
		if (tutor == null) {
			return;
		}

		req.setAttribute("tutor", tutor);
		req.getRequestDispatcher("/vista/tutor/dashboard.jsp").forward(req, resp);
	}

	private void horarios(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Tutor tutor = requerirTutor(req, resp);
		if (tutor == null) {
			return;
		}

		req.setAttribute("tutor", tutor);
		req.setAttribute("slotsSeleccionados", disponibilidadDAO.slotsComoCadena(tutor.getId()));

		if ("ok".equals(req.getParameter("mensaje"))) {
			req.setAttribute("mensaje", "Disponibilidad guardada correctamente.");
		}
		if (req.getParameter("error") != null && !req.getParameter("error").isBlank()) {
			req.setAttribute("error", req.getParameter("error"));
		}

		req.getRequestDispatcher("/vista/tutor/horarios.jsp").forward(req, resp);
	}

	private void guardarHorarios(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Tutor tutor = requerirTutor(req, resp);
		if (tutor == null) {
			return;
		}

		String slots = req.getParameter("slots");
		if (slots == null) {
			slots = "";
		}

		try {
			disponibilidadDAO.reemplazarSlots(tutor.getId(), slots.trim());
			HttpSession session = req.getSession(false);
			if (session != null) {
				session.removeAttribute("horariosTemporales");
			}
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=horarios&mensaje=ok");
		} catch (IllegalArgumentException e) {
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=horarios&error="
					+ java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
		} catch (RuntimeException e) {
			getServletContext().log("Error al guardar disponibilidad", e);
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=horarios&error="
					+ java.net.URLEncoder.encode("No se pudo guardar la disponibilidad.",
							java.nio.charset.StandardCharsets.UTF_8));
		}
	}

	private Tutor requerirTutor(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession(false);
		Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");

		if (usuario == null) {
			resp.sendRedirect(req.getContextPath() + "/login?ruta=ingresar");
			return null;
		}

		if (!(usuario instanceof Tutor tutor)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso solo para tutores");
			return null;
		}

		return tutor;
	}
}
