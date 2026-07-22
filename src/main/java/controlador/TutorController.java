package controlador;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.AsignaturaDAO;
import modelo.dao.DisponibilidadDAO;
import modelo.dao.SolicitudDAO;
import modelo.dao.TutorDAO;
import modelo.entities.Asignatura;
import modelo.entities.EstadoSolicitud;
import modelo.entities.Tutor;
import modelo.entities.Usuario;

@WebServlet("/tutor")
public class TutorController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO();
	private final TutorDAO tutorDAO = new TutorDAO();
	private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
	private final SolicitudDAO solicitudDAO = new SolicitudDAO();

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
			case "materias" -> materias(req, resp);
			case "guardar-materias" -> guardarMaterias(req, resp);
			case "solicitudes" -> solicitudes(req, resp);
			case "responder-solicitud" -> responderSolicitud(req, resp);
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
		}
	}

	private void inicio(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Tutor tutorSesion = requerirTutor(req, resp);
		if (tutorSesion == null) {
			return;
		}

		Tutor tutor = tutorDAO.buscarPorIdConMaterias(tutorSesion.getId());
		if (tutor == null) {
			resp.sendRedirect(req.getContextPath() + "/login?ruta=ingresar");
			return;
		}

		req.getSession(true).setAttribute("usuario", tutor);
		req.setAttribute("tutor", tutor);
		req.setAttribute("materiasTutor", tutor.getMaterias().stream()
				.sorted(java.util.Comparator
						.comparingInt(Asignatura::getSemestre)
						.thenComparing(Asignatura::getCodigo))
				.toList());
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

	private void materias(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Tutor tutorSesion = requerirTutor(req, resp);
		if (tutorSesion == null) {
			return;
		}

		Tutor tutor = tutorDAO.buscarPorIdConMaterias(tutorSesion.getId());
		if (tutor == null || tutor.getCarrera() == null) {
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=inicio");
			return;
		}

		List<Asignatura> disponibles = asignaturaDAO.listarAprobadasParaTutor(
				tutor.getCarrera().getId(), tutor.getSemestre());

		Map<Integer, List<Asignatura>> porSemestre = new LinkedHashMap<>();
		for (Asignatura a : disponibles) {
			porSemestre.computeIfAbsent(a.getSemestre(), k -> new ArrayList<>()).add(a);
		}

		Set<Long> idsSeleccionados = tutor.getMaterias().stream()
				.map(Asignatura::getId)
				.collect(Collectors.toSet());

		req.setAttribute("tutor", tutor);
		req.setAttribute("materiasPorSemestre", porSemestre);
		req.setAttribute("idsSeleccionados", idsSeleccionados);

		if ("ok".equals(req.getParameter("mensaje"))) {
			req.setAttribute("mensaje", "Materias actualizadas correctamente.");
		}
		if (req.getParameter("error") != null && !req.getParameter("error").isBlank()) {
			req.setAttribute("error", req.getParameter("error"));
		}

		req.getRequestDispatcher("/vista/tutor/materias.jsp").forward(req, resp);
	}

	private void guardarMaterias(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Tutor tutor = requerirTutor(req, resp);
		if (tutor == null) {
			return;
		}

		String[] materiasParams = req.getParameterValues("materias");
		Set<Long> ids = new HashSet<>();
		if (materiasParams != null) {
			for (String raw : materiasParams) {
				try {
					ids.add(Long.valueOf(raw));
				} catch (NumberFormatException ignored) {
					// skip
				}
			}
		}

		try {
			tutorDAO.reemplazarMaterias(tutor.getId(), ids);
			Tutor actualizado = tutorDAO.buscarPorIdConMaterias(tutor.getId());
			if (actualizado != null) {
				req.getSession(true).setAttribute("usuario", actualizado);
			}
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=materias&mensaje=ok");
		} catch (IllegalArgumentException e) {
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=materias&error="
					+ java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
		} catch (RuntimeException e) {
			getServletContext().log("Error al guardar materias del tutor", e);
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=materias&error="
					+ java.net.URLEncoder.encode("No se pudieron guardar las materias.",
							java.nio.charset.StandardCharsets.UTF_8));
		}
	}

	private void solicitudes(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Tutor tutor = requerirTutor(req, resp);
		if (tutor == null) {
			return;
		}

		req.setAttribute("tutor", tutor);
		req.setAttribute("solicitudes", solicitudDAO.listarPorTutor(tutor.getId()));

		if ("ok".equals(req.getParameter("mensaje"))) {
			req.setAttribute("mensaje", "Solicitud actualizada correctamente.");
		}
		if (req.getParameter("error") != null && !req.getParameter("error").isBlank()) {
			req.setAttribute("error", req.getParameter("error"));
		}

		req.getRequestDispatcher("/vista/tutor/solicitudes.jsp").forward(req, resp);
	}

	private void responderSolicitud(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Tutor tutor = requerirTutor(req, resp);
		if (tutor == null) {
			return;
		}

		Long solicitudId = null;
		String idRaw = req.getParameter("solicitudId");
		if (idRaw != null && !idRaw.isBlank()) {
			try {
				solicitudId = Long.valueOf(idRaw.trim());
			} catch (NumberFormatException ignored) {
				solicitudId = null;
			}
		}

		String accion = req.getParameter("accion");
		EstadoSolicitud nuevoEstado = null;
		if ("aceptar".equalsIgnoreCase(accion)) {
			nuevoEstado = EstadoSolicitud.ACEPTADA;
		} else if ("rechazar".equalsIgnoreCase(accion)) {
			nuevoEstado = EstadoSolicitud.RECHAZADA;
		}

		if (solicitudId == null || nuevoEstado == null) {
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=solicitudes&error="
					+ java.net.URLEncoder.encode("Acción inválida.",
							java.nio.charset.StandardCharsets.UTF_8));
			return;
		}

		try {
			solicitudDAO.actualizarEstado(solicitudId, tutor.getId(), nuevoEstado);
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=solicitudes&mensaje=ok");
		} catch (IllegalArgumentException | IllegalStateException e) {
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=solicitudes&error="
					+ java.net.URLEncoder.encode(e.getMessage(),
							java.nio.charset.StandardCharsets.UTF_8));
		} catch (RuntimeException e) {
			getServletContext().log("Error al responder solicitud", e);
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=solicitudes&error="
					+ java.net.URLEncoder.encode("No se pudo actualizar la solicitud.",
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
