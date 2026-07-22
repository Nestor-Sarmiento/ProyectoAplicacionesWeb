package modelo.services;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import modelo.dao.SesionLlamadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.entities.Estudiante;
import modelo.entities.LlamadaAcceso;
import modelo.entities.LlamadaTokenRespuesta;
import modelo.entities.SesionLlamada;
import modelo.entities.SolicitudTutoria;
import modelo.entities.Tutor;
import modelo.entities.Usuario;
import util.EmailService;
import util.LiveKitTokenService;
import util.LlamadaSalaUtil;

public class LlamadaService {

	private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final SesionLlamadaDAO sesionLlamadaDAO = new SesionLlamadaDAO();
	private final LiveKitTokenService liveKitTokenService = new LiveKitTokenService();

	public static class ResultadoLlamada {
		public final SesionLlamada sesion;
		public final String linkTutor;
		public final String linkEstudiante;

		public ResultadoLlamada(SesionLlamada sesion, String linkTutor, String linkEstudiante) {
			this.sesion = sesion;
			this.linkTutor = linkTutor;
			this.linkEstudiante = linkEstudiante;
		}
	}

	public ResultadoLlamada crearLlamada(Long tutorId, Long studentId) {
		return crearLlamada(tutorId, studentId, null);
	}

	public ResultadoLlamada crearLlamadaParaSolicitud(SolicitudTutoria solicitud) {
		if (solicitud == null || solicitud.getTutor() == null || solicitud.getEstudiante() == null) {
			throw new IllegalArgumentException("La solicitud debe incluir tutor y estudiante.");
		}
		return crearLlamada(
				solicitud.getTutor().getId(),
				solicitud.getEstudiante().getId(),
				solicitud);
	}

	/**
	 * Enlaces de unirse indexados por id de solicitud.
	 * {@code paraTutor=true} usa el token del tutor; si no, el del estudiante.
	 */
	public Map<Long, String> mapearEnlacesUnirse(Collection<SolicitudTutoria> solicitudes, boolean paraTutor) {
		if (solicitudes == null || solicitudes.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Long> ids = solicitudes.stream()
				.filter(s -> s != null && s.getId() != null)
				.map(SolicitudTutoria::getId)
				.distinct()
				.toList();
		if (ids.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Long, SesionLlamada> sesiones = sesionLlamadaDAO.mapearPorSolicitudIds(ids);
		Map<Long, String> enlaces = new HashMap<>();
		for (Map.Entry<Long, SesionLlamada> entry : sesiones.entrySet()) {
			String enlace = paraTutor
					? LlamadaSalaUtil.enlaceTutor(entry.getValue())
					: LlamadaSalaUtil.enlaceEstudiante(entry.getValue());
			if (enlace != null && !enlace.isBlank()) {
				enlaces.put(entry.getKey(), enlace);
			}
		}
		return enlaces;
	}

	public ResultadoLlamada crearLlamada(Long tutorId, Long studentId, SolicitudTutoria solicitud) {
		Optional<Usuario> tutorOpt = usuarioDAO.buscarPorId(tutorId);
		Optional<Usuario> studentOpt = usuarioDAO.buscarPorId(studentId);

		Usuario tutor = tutorOpt.orElseThrow(
				() -> new IllegalArgumentException("El tutor indicado no es válido."));
		Usuario estudiante = studentOpt.orElseThrow(
				() -> new IllegalArgumentException("El estudiante indicado no es válido."));

		if (!(tutor instanceof Tutor)) {
			throw new IllegalArgumentException("El tutor indicado no es válido.");
		}
		if (!(estudiante instanceof Estudiante)) {
			throw new IllegalArgumentException("El estudiante indicado no es válido.");
		}

		SesionLlamada sesion = new SesionLlamada();
		sesion.setTutorId(tutorId);
		sesion.setStudentId(studentId);
		if (solicitud != null) {
			sesion.setSolicitudId(solicitud.getId());
		}
		sesion.setCompletada(false);
		sesionLlamadaDAO.guardar(sesion);

		LlamadaTokenRespuesta respuestaTokens;
		try {
			respuestaTokens = liveKitTokenService.crearSalaYTokens(sesion.getId(), tutorId, studentId);
		} catch (RuntimeException e) {
			System.err.println("ERROR al crear sala de llamada para sesionId=" + sesion.getId()
					+ ": " + e.getMessage());
			e.printStackTrace();
			String causa = e.getMessage() != null && !e.getMessage().isBlank()
					? e.getMessage()
					: e.getClass().getSimpleName();
			throw new IllegalStateException("No se pudo crear la sala de llamada: " + causa, e);
		}

		sesion.setRoomName(respuestaTokens.roomName());
		sesion.setLivekitUrl(respuestaTokens.livekitUrl());
		sesion.setTutorToken(respuestaTokens.tutorToken());
		sesion.setStudentToken(respuestaTokens.studentToken());
		sesionLlamadaDAO.actualizar(sesion);

		LlamadaAcceso accesoTutor = LlamadaSalaUtil.crearAcceso(
				"Tutor", tutor.getEmail(), respuestaTokens, sesion.getId(), respuestaTokens.tutorToken());
		LlamadaAcceso accesoEstudiante = LlamadaSalaUtil.crearAcceso(
				"Estudiante", estudiante.getEmail(), respuestaTokens, sesion.getId(), respuestaTokens.studentToken());

		String materia = "Tutoría OwlShare";
		String fecha = "";
		String horario = "";
		if (solicitud != null) {
			if (solicitud.getAsignatura() != null) {
				materia = solicitud.getAsignatura().getCodigo() + " — " + solicitud.getAsignatura().getNombre();
			}
			if (solicitud.getFechaSesion() != null) {
				fecha = solicitud.getFechaSesion().format(FECHA);
			}
			if (solicitud.getDisponibilidad() != null) {
				horario = solicitud.getDisponibilidad().getDiaSemana().getEtiqueta()
						+ " · " + solicitud.getDisponibilidad().getHoraInicio()
						+ " – " + solicitud.getDisponibilidad().getHoraFin();
			}
		}

		EmailService.enviarEnlaceLlamada(
				tutor.getEmail(), tutor.getNombreCompleto(), "Tutor",
				accesoTutor.enlace(), materia, fecha, horario);
		EmailService.enviarEnlaceLlamada(
				estudiante.getEmail(), estudiante.getNombreCompleto(), "Estudiante",
				accesoEstudiante.enlace(), materia, fecha, horario);

		return new ResultadoLlamada(sesion, accesoTutor.enlace(), accesoEstudiante.enlace());
	}
}
