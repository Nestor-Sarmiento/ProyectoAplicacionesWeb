package controlador;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
import modelo.entities.DiaSemana;
import modelo.entities.Disponibilidad;
import modelo.entities.Estudiante;
import modelo.entities.SolicitudTutoria;
import modelo.entities.Tutor;
import modelo.entities.Usuario;
import modelo.services.LlamadaService;
import util.EnvLoader;

@WebServlet("/estudiante")
public class EstudianteController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter FECHA_ISO = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM");

	private final TutorDAO tutorDAO = new TutorDAO();
	private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
	private final DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO();
	private final SolicitudDAO solicitudDAO = new SolicitudDAO();
	private final LlamadaService llamadaService = new LlamadaService();

	static {
		EnvLoader.ensureLoaded();
	}

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
			case "buscar-tutor" -> buscarTutor(req, resp);
			case "detalle-tutor" -> detalleTutor(req, resp);
			case "solicitar-tutoria" -> mostrarSolicitarTutoria(req, resp);
			case "enviar-solicitud" -> enviarSolicitud(req, resp);
			case "solicitudes" -> solicitudes(req, resp);
			case "cancelar-solicitud" -> cancelarSolicitud(req, resp);
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
		}
	}

	private void inicio(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		req.setAttribute("estudiante", estudiante);
		List<SolicitudTutoria> proximas = solicitudDAO.listarProximasSesionesEstudiante(estudiante.getId());
		req.setAttribute("proximasSesiones", proximas);
		req.setAttribute("enlacesUnirse", llamadaService.mapearEnlacesUnirse(proximas, false));
		req.getRequestDispatcher("/vista/estudiante/dashboard.jsp").forward(req, resp);
	}

	private void solicitudes(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		req.setAttribute("estudiante", estudiante);
		List<SolicitudTutoria> solicitudes = solicitudDAO.listarPorEstudiante(estudiante.getId());
		req.setAttribute("solicitudes", solicitudes);
		req.setAttribute("enlacesUnirse", llamadaService.mapearEnlacesUnirse(solicitudes, false));

		if ("ok".equals(req.getParameter("mensaje"))) {
			req.setAttribute("mensaje", "Solicitud cancelada correctamente.");
		}
		if (req.getParameter("error") != null && !req.getParameter("error").isBlank()) {
			req.setAttribute("error", req.getParameter("error"));
		}

		req.getRequestDispatcher("/vista/estudiante/solicitudes.jsp").forward(req, resp);
	}

	private void cancelarSolicitud(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		Long solicitudId = parseLong(req.getParameter("solicitudId"));
		String redirect = req.getContextPath() + "/estudiante?ruta=solicitudes";
		if (solicitudId == null) {
			resp.sendRedirect(redirect + "&error=" + encode("Solicitud inválida."));
			return;
		}

		try {
			solicitudDAO.cancelarPorEstudiante(solicitudId, estudiante.getId());
			resp.sendRedirect(redirect + "&mensaje=ok");
		} catch (IllegalArgumentException | IllegalStateException e) {
			resp.sendRedirect(redirect + "&error=" + encode(e.getMessage()));
		} catch (RuntimeException e) {
			getServletContext().log("Error al cancelar solicitud", e);
			resp.sendRedirect(redirect + "&error="
					+ encode("No se pudo cancelar la solicitud. Intenta de nuevo."));
		}
	}

	private void buscarTutor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		if (estudiante.getCarrera() == null) {
			req.setAttribute("estudiante", estudiante);
			req.setAttribute("error", "Tu cuenta no tiene una carrera asignada.");
			req.setAttribute("materiasFiltro", List.of());
			req.setAttribute("tutores", List.of());
			req.setAttribute("busquedaRealizada", false);
			req.getRequestDispatcher("/vista/estudiante/buscar-tutor.jsp").forward(req, resp);
			return;
		}

		Long asignaturaId = parseLong(req.getParameter("asignaturaId"));
		List<Asignatura> materiasFiltro = asignaturaDAO.listarPorCarreraHastaSemestre(
				estudiante.getCarrera().getId(), estudiante.getSemestre());

		Asignatura materiaSeleccionada = null;
		if (asignaturaId != null) {
			final Long idMateria = asignaturaId;
			materiaSeleccionada = materiasFiltro.stream()
					.filter(a -> a.getId().equals(idMateria))
					.findFirst()
					.orElse(null);
			if (materiaSeleccionada == null) {
				asignaturaId = null;
			}
		}

		List<Tutor> tutores;
		if (asignaturaId != null) {
			tutores = tutorDAO.buscar(estudiante.getCarrera().getId(), asignaturaId);
		} else {
			tutores = tutorDAO.buscar(
					estudiante.getCarrera().getId(), null, estudiante.getSemestre());
		}

		req.setAttribute("estudiante", estudiante);
		req.setAttribute("materiasFiltro", materiasFiltro);
		req.setAttribute("materiasJson", construirMateriasJson(materiasFiltro));
		req.setAttribute("asignaturaIdSeleccionada", asignaturaId);
		req.setAttribute("materiaSeleccionada", materiaSeleccionada);
		req.setAttribute("tutores", tutores);
		req.setAttribute("busquedaRealizada", true);
		req.getRequestDispatcher("/vista/estudiante/buscar-tutor.jsp").forward(req, resp);
	}

	private void detalleTutor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		Long tutorId = parseLong(req.getParameter("id"));
		if (tutorId == null) {
			resp.sendRedirect(req.getContextPath() + "/estudiante?ruta=buscar-tutor");
			return;
		}

		Tutor tutor = tutorDAO.buscarPorIdConMaterias(tutorId);
		if (tutor == null || !tutor.isActivo()) {
			req.setAttribute("error", "Tutor no encontrado.");
			buscarTutor(req, resp);
			return;
		}

		List<Asignatura> materiasOrdenadas = tutor.getMaterias().stream()
				.sorted(Comparator.comparingInt(Asignatura::getSemestre)
						.thenComparing(Asignatura::getCodigo))
				.toList();
		List<Asignatura> materiasElegibles = filtrarMateriasParaEstudiante(tutor, estudiante);

		Long asignaturaId = parseLong(req.getParameter("asignaturaId"));
		Asignatura materiaBusqueda = resolverMateriaBusqueda(materiasElegibles, asignaturaId);

		List<Disponibilidad> horarios = listarHorariosOrdenados(tutorId);
		Map<DiaSemana, List<Disponibilidad>> horariosPorDia = agruparPorDia(horarios);

		req.setAttribute("estudiante", estudiante);
		req.setAttribute("tutor", tutor);
		req.setAttribute("materiasTutor", materiasOrdenadas);
		req.setAttribute("puedeSolicitar",
				!materiasElegibles.isEmpty() && !horarios.isEmpty());
		req.setAttribute("horariosTutor", horarios);
		req.setAttribute("horariosPorDia", horariosPorDia);
		req.setAttribute("asignaturaIdSeleccionada",
				materiaBusqueda != null ? materiaBusqueda.getId() : null);
		req.setAttribute("materiaBusqueda", materiaBusqueda);

		if ("ok".equals(req.getParameter("mensaje"))) {
			req.setAttribute("mensaje", "Solicitud de tutoría enviada. Quedó en estado pendiente.");
		}
		if (req.getParameter("error") != null && !req.getParameter("error").isBlank()) {
			req.setAttribute("error", req.getParameter("error"));
		}

		req.getRequestDispatcher("/vista/estudiante/detalle-tutor.jsp").forward(req, resp);
	}

	private void mostrarSolicitarTutoria(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		Long tutorId = parseLong(req.getParameter("tutorId"));
		if (tutorId == null) {
			tutorId = parseLong(req.getParameter("id"));
		}
		if (tutorId == null) {
			resp.sendRedirect(req.getContextPath() + "/estudiante?ruta=buscar-tutor");
			return;
		}

		Tutor tutor = tutorDAO.buscarPorIdConMaterias(tutorId);
		if (tutor == null || !tutor.isActivo()) {
			resp.sendRedirect(req.getContextPath() + "/estudiante?ruta=buscar-tutor");
			return;
		}

		List<Asignatura> materiasElegibles = filtrarMateriasParaEstudiante(tutor, estudiante);
		Long asignaturaId = parseLong(req.getParameter("asignaturaId"));
		Asignatura materiaFija = null;
		if (asignaturaId != null) {
			final Long idMateria = asignaturaId;
			materiaFija = materiasElegibles.stream()
					.filter(a -> a.getId().equals(idMateria))
					.findFirst()
					.orElse(null);
		}
		boolean materiaBloqueada = materiaFija != null;

		List<Disponibilidad> horarios = listarHorariosOrdenados(tutorId);
		LocalDate hoy = LocalDate.now();
		LocalDate lunesActual = hoy.with(DayOfWeek.MONDAY);
		LocalDate lunesSiguiente = lunesActual.plusWeeks(1);
		LocalDate domingoSiguiente = lunesSiguiente.plusDays(6);

		Set<String> ocupados = solicitudDAO.clavesSlotsOcupados(
				tutorId, lunesActual, domingoSiguiente);

		String slotsDisponiblesCsv = horarios.stream()
				.map(Disponibilidad::toSlotKey)
				.collect(Collectors.joining(","));
		String slotsOcupadosCsv = ocupados.stream().sorted().collect(Collectors.joining(","));

		StringBuilder dispJson = new StringBuilder("{");
		boolean primero = true;
		for (Disponibilidad d : horarios) {
			if (!primero) {
				dispJson.append(',');
			}
			primero = false;
			dispJson.append('"').append(escaparJson(d.toSlotKey())).append("\":").append(d.getId());
		}
		dispJson.append('}');

		req.setAttribute("estudiante", estudiante);
		req.setAttribute("tutor", tutor);
		req.setAttribute("materiasElegibles", materiasElegibles);
		req.setAttribute("materiaFija", materiaFija);
		req.setAttribute("materiaBloqueada", materiaBloqueada);
		req.setAttribute("asignaturaIdSeleccionada",
				materiaFija != null ? materiaFija.getId() : null);
		req.setAttribute("semanasJson", construirSemanasJson(lunesActual, lunesSiguiente));
		req.setAttribute("slotsDisponibles", slotsDisponiblesCsv);
		req.setAttribute("slotsOcupados", slotsOcupadosCsv);
		req.setAttribute("disponibilidadJson", dispJson.toString());
		req.setAttribute("hoyIso", hoy.format(FECHA_ISO));
		req.setAttribute("horaActual", LocalTime.now().withSecond(0).withNano(0).toString().substring(0, 5));

		if (req.getParameter("error") != null && !req.getParameter("error").isBlank()) {
			req.setAttribute("error", req.getParameter("error"));
		}

		req.getRequestDispatcher("/vista/estudiante/solicitar-tutoria.jsp").forward(req, resp);
	}

	private String construirSemanasJson(LocalDate lunesActual, LocalDate lunesSiguiente) {
		StringBuilder sb = new StringBuilder("[");
		sb.append(construirSemanaJson(0, "Semana actual", lunesActual));
		sb.append(',');
		sb.append(construirSemanaJson(1, "Semana siguiente", lunesSiguiente));
		sb.append(']');
		return sb.toString();
	}

	private String construirSemanaJson(int indice, String etiqueta, LocalDate lunes) {
		LocalDate domingo = lunes.plusDays(6);
		StringBuilder sb = new StringBuilder();
		sb.append("{\"indice\":").append(indice)
				.append(",\"etiqueta\":\"").append(escaparJson(etiqueta)).append('"')
				.append(",\"inicio\":\"").append(lunes.format(FECHA_CORTA)).append('"')
				.append(",\"fin\":\"").append(domingo.format(FECHA_CORTA)).append('"')
				.append(",\"columnas\":[");
		for (int i = 0; i < 7; i++) {
			if (i > 0) {
				sb.append(',');
			}
			LocalDate fecha = lunes.plusDays(i);
			DiaSemana dia = DiaSemana.from(fecha.getDayOfWeek());
			sb.append("{\"dia\":\"").append(dia.name()).append('"')
					.append(",\"etiqueta\":\"").append(escaparJson(dia.getEtiqueta().substring(0, 3))).append('"')
					.append(",\"fecha\":\"").append(fecha.format(FECHA_ISO)).append('"')
					.append(",\"fechaCorta\":\"").append(fecha.format(FECHA_CORTA)).append('"')
					.append('}');
		}
		sb.append("]}");
		return sb.toString();
	}

	private void enviarSolicitud(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		Long tutorId = parseLong(req.getParameter("tutorId"));
		Long asignaturaId = parseLong(req.getParameter("asignaturaId"));
		Long disponibilidadId = parseLong(req.getParameter("disponibilidadId"));
		LocalDate fechaSesion = parseFecha(req.getParameter("fecha"));
		String mensaje = req.getParameter("mensaje");

		String redirectSolicitar = req.getContextPath() + "/estudiante?ruta=solicitar-tutoria"
				+ "&tutorId=" + (tutorId != null ? tutorId : "")
				+ (asignaturaId != null ? "&asignaturaId=" + asignaturaId : "");

		String redirectDetalle = req.getContextPath() + "/estudiante?ruta=detalle-tutor&id="
				+ (tutorId != null ? tutorId : "")
				+ (asignaturaId != null ? "&asignaturaId=" + asignaturaId : "");

		if (tutorId == null || asignaturaId == null || disponibilidadId == null || fechaSesion == null) {
			resp.sendRedirect(redirectSolicitar + "&error="
					+ encode("Completa materia, horario y mensaje para continuar."));
			return;
		}

		try {
			solicitudDAO.crear(
					estudiante.getId(), tutorId, asignaturaId, disponibilidadId, fechaSesion, mensaje);
			resp.sendRedirect(redirectDetalle + "&mensaje=ok");
		} catch (IllegalArgumentException | IllegalStateException e) {
			resp.sendRedirect(redirectSolicitar + "&error=" + encode(e.getMessage()));
		} catch (RuntimeException e) {
			getServletContext().log("Error al crear solicitud de tutoría", e);
			resp.sendRedirect(redirectSolicitar + "&error="
					+ encode("No se pudo enviar la solicitud. Intenta de nuevo."));
		}
	}

	private List<Asignatura> filtrarMateriasParaEstudiante(Tutor tutor, Estudiante estudiante) {
		int semestreMax = estudiante.getSemestre();
		return tutor.getMaterias().stream()
				.filter(m -> m.getSemestre() <= semestreMax)
				.sorted(Comparator.comparingInt(Asignatura::getSemestre)
						.thenComparing(Asignatura::getCodigo))
				.toList();
	}

	private Asignatura resolverMateriaBusqueda(List<Asignatura> materias, Long asignaturaId) {
		if (asignaturaId == null) {
			return null;
		}
		return materias.stream()
				.filter(a -> a.getId().equals(asignaturaId))
				.findFirst()
				.orElse(null);
	}

	private List<Disponibilidad> listarHorariosOrdenados(Long tutorId) {
		return disponibilidadDAO.listarPorTutor(tutorId).stream()
				.sorted(Comparator
						.comparing((Disponibilidad d) -> d.getDiaSemana().ordinal())
						.thenComparing(Disponibilidad::getHoraInicio))
				.toList();
	}

	private Map<DiaSemana, List<Disponibilidad>> agruparPorDia(List<Disponibilidad> horarios) {
		Map<DiaSemana, List<Disponibilidad>> porDia = new LinkedHashMap<>();
		for (DiaSemana dia : DiaSemana.values()) {
			List<Disponibilidad> delDia = horarios.stream()
					.filter(d -> d.getDiaSemana() == dia)
					.toList();
			if (!delDia.isEmpty()) {
				porDia.put(dia, delDia);
			}
		}
		return porDia;
	}

	private Estudiante requerirEstudiante(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession(false);
		Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");

		if (usuario == null) {
			resp.sendRedirect(req.getContextPath() + "/login?ruta=ingresar");
			return null;
		}

		if (!(usuario instanceof Estudiante estudiante)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso solo para estudiantes");
			return null;
		}

		return estudiante;
	}

	private Long parseLong(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Long.valueOf(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private LocalDate parseFecha(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(value.trim(), FECHA_ISO);
		} catch (RuntimeException e) {
			return null;
		}
	}

	private String encode(String valor) {
		return java.net.URLEncoder.encode(valor == null ? "" : valor, java.nio.charset.StandardCharsets.UTF_8);
	}

	private String construirMateriasJson(List<Asignatura> materias) {
		StringBuilder sb = new StringBuilder("[");
		boolean primero = true;
		for (Asignatura a : materias) {
			if (!primero) {
				sb.append(',');
			}
			primero = false;
			String etiqueta = a.getCodigo() + " — " + a.getNombre();
			sb.append("{\"id\":").append(a.getId())
					.append(",\"codigo\":\"").append(escaparJson(a.getCodigo())).append('"')
					.append(",\"nombre\":\"").append(escaparJson(a.getNombre())).append('"')
					.append(",\"semestre\":").append(a.getSemestre())
					.append(",\"etiqueta\":\"").append(escaparJson(etiqueta)).append('"')
					.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private String escaparJson(String valor) {
		if (valor == null) {
			return "";
		}
		return valor
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r");
	}
}
