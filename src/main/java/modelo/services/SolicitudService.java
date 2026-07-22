package modelo.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelo.dao.AsignaturaDAO;
import modelo.dao.DisponibilidadDAO;
import modelo.dao.SolicitudDAO;
import modelo.dao.TutorDAO;
import modelo.dao.UsuarioDAO;
import modelo.entities.Asignatura;
import modelo.entities.DiaSemana;
import modelo.entities.Disponibilidad;
import modelo.entities.EstadoSolicitud;
import modelo.entities.Estudiante;
import modelo.entities.SolicitudTutoria;
import modelo.entities.Tutor;

public class SolicitudService {

	private final SolicitudDAO solicitudDAO = new SolicitudDAO();
	private final TutorDAO tutorDAO = new TutorDAO();
	private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
	private final DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	public static String claveSlot(Long disponibilidadId, LocalDate fecha) {
		return disponibilidadId + "|" + fecha;
	}

	public SolicitudTutoria crear(
			Long estudianteId,
			Long tutorId,
			Long asignaturaId,
			Long disponibilidadId,
			LocalDate fechaSesion,
			String mensaje) {
		if (estudianteId == null || tutorId == null || asignaturaId == null
				|| disponibilidadId == null || fechaSesion == null) {
			throw new IllegalArgumentException("Estudiante, tutor, materia, horario y fecha son obligatorios.");
		}
		if (mensaje == null || mensaje.isBlank()) {
			throw new IllegalArgumentException("El mensaje es obligatorio.");
		}
		String mensajeLimpio = mensaje.trim();
		if (mensajeLimpio.length() < 10) {
			throw new IllegalArgumentException("El mensaje debe tener al menos 10 caracteres.");
		}
		if (mensajeLimpio.length() > 500) {
			throw new IllegalArgumentException("El mensaje no puede superar 500 caracteres.");
		}

		Estudiante estudiante = usuarioDAO.buscarPorId(estudianteId)
				.filter(Estudiante.class::isInstance)
				.map(Estudiante.class::cast)
				.orElse(null);
		Tutor tutor = tutorDAO.buscarPorIdConMaterias(tutorId);
		Asignatura asignatura = asignaturaDAO.buscarPorId(asignaturaId);
		Disponibilidad disponibilidad = disponibilidadDAO.buscarPorId(disponibilidadId);

		if (estudiante == null || tutor == null || asignatura == null) {
			throw new IllegalArgumentException("Datos de solicitud inválidos.");
		}
		if (!tutor.isActivo()) {
			throw new IllegalArgumentException("El tutor no está disponible.");
		}
		if (tutor.getMaterias() == null || tutor.getMaterias().stream()
				.noneMatch(m -> m.getId().equals(asignaturaId))) {
			throw new IllegalArgumentException("El tutor no ofrece esa materia.");
		}
		if (asignatura.getSemestre() > estudiante.getSemestre()) {
			throw new IllegalArgumentException("Esa materia no está habilitada para tu semestre.");
		}
		if (disponibilidad == null || !disponibilidad.isActivo()) {
			throw new IllegalArgumentException("Horario de disponibilidad inválido.");
		}
		if (!disponibilidad.getTutor().getId().equals(tutorId)) {
			throw new IllegalArgumentException("Horario de disponibilidad inválido.");
		}

		DiaSemana diaEsperado = DiaSemana.from(fechaSesion.getDayOfWeek());
		if (disponibilidad.getDiaSemana() != diaEsperado) {
			throw new IllegalArgumentException("La fecha no corresponde al día del horario seleccionado.");
		}

		LocalDate hoy = LocalDate.now();
		LocalDate lunesActual = hoy.with(java.time.DayOfWeek.MONDAY);
		LocalDate domingoSiguiente = lunesActual.plusWeeks(1).plusDays(6);
		if (fechaSesion.isBefore(lunesActual) || fechaSesion.isAfter(domingoSiguiente)) {
			throw new IllegalArgumentException(
					"Solo puedes solicitar horarios de la semana actual o la siguiente.");
		}
		if (fechaSesion.isBefore(hoy)) {
			throw new IllegalArgumentException("No puedes solicitar un horario que ya pasó.");
		}
		if (fechaSesion.equals(hoy)) {
			LocalTime horaInicio = LocalTime.parse(disponibilidad.getHoraInicio());
			if (!horaInicio.isAfter(LocalTime.now())) {
				throw new IllegalArgumentException("No puedes solicitar un horario que ya pasó.");
			}
		}

		if (solicitudDAO.contarActivasPorSlot(tutorId, disponibilidadId, fechaSesion) > 0) {
			throw new IllegalArgumentException("Ese horario ya fue solicitado por otro estudiante.");
		}

		SolicitudTutoria solicitud = new SolicitudTutoria();
		solicitud.setEstudiante(estudiante);
		solicitud.setTutor(tutor);
		solicitud.setAsignatura(asignatura);
		solicitud.setDisponibilidad(disponibilidad);
		solicitud.setFechaSesion(fechaSesion);
		solicitud.setMensaje(mensajeLimpio);
		solicitud.setEstado(EstadoSolicitud.PENDIENTE);
		solicitudDAO.guardar(solicitud);
		return solicitud;
	}

	public Set<String> clavesSlotsOcupados(Long tutorId, LocalDate inicioSemana, LocalDate finSemana) {
		if (tutorId == null || inicioSemana == null || finSemana == null) {
			return Set.of();
		}
		Set<String> claves = new HashSet<>();
		for (SolicitudTutoria s : solicitudDAO.listarActivasPorTutorEnRango(tutorId, inicioSemana, finSemana)) {
			if (s.getDisponibilidad() != null) {
				claves.add(claveSlot(s.getDisponibilidad().getId(), s.getFechaSesion()));
			}
		}
		return claves;
	}

	public List<SolicitudTutoria> listarPorTutor(Long tutorId) {
		return solicitudDAO.listarPorTutor(tutorId);
	}

	public List<SolicitudTutoria> listarPorEstudiante(Long estudianteId) {
		return solicitudDAO.listarPorEstudiante(estudianteId);
	}

	public List<SolicitudTutoria> listarProximasSesionesEstudiante(Long estudianteId) {
		return filtrarProximasSesiones(solicitudDAO.listarAceptadasDesdeHoy(estudianteId, null));
	}

	public List<SolicitudTutoria> listarProximasSesionesTutor(Long tutorId) {
		return filtrarProximasSesiones(solicitudDAO.listarAceptadasDesdeHoy(null, tutorId));
	}

	public SolicitudTutoria responderSolicitud(Long solicitudId, Long tutorId, EstadoSolicitud nuevoEstado) {
		if (solicitudId == null || tutorId == null || nuevoEstado == null) {
			throw new IllegalArgumentException("Datos incompletos.");
		}

		SolicitudTutoria solicitud = solicitudDAO.buscarPorIdConRelaciones(solicitudId);
		if (solicitud == null || !solicitud.getTutor().getId().equals(tutorId)) {
			throw new IllegalArgumentException("Solicitud no encontrada.");
		}
		if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
			throw new IllegalStateException("Solo se pueden responder solicitudes pendientes.");
		}

		solicitud.setEstado(nuevoEstado);
		solicitudDAO.actualizar(solicitud);
		return solicitud;
	}

	public void cancelarPorEstudiante(Long solicitudId, Long estudianteId) {
		if (solicitudId == null || estudianteId == null) {
			throw new IllegalArgumentException("Datos incompletos.");
		}

		SolicitudTutoria solicitud = solicitudDAO.buscarPorId(solicitudId);
		if (solicitud == null || !solicitud.getEstudiante().getId().equals(estudianteId)) {
			throw new IllegalArgumentException("Solicitud no encontrada.");
		}
		EstadoSolicitud estado = solicitud.getEstado();
		if (estado != EstadoSolicitud.PENDIENTE && estado != EstadoSolicitud.ACEPTADA) {
			throw new IllegalStateException(
					"Solo puedes cancelar solicitudes pendientes o sesiones aceptadas.");
		}

		solicitud.setEstado(EstadoSolicitud.CANCELADA);
		solicitudDAO.actualizar(solicitud);
	}

	public void cancelarPorTutor(Long solicitudId, Long tutorId) {
		if (solicitudId == null || tutorId == null) {
			throw new IllegalArgumentException("Datos incompletos.");
		}

		SolicitudTutoria solicitud = solicitudDAO.buscarPorId(solicitudId);
		if (solicitud == null || !solicitud.getTutor().getId().equals(tutorId)) {
			throw new IllegalArgumentException("Solicitud no encontrada.");
		}
		if (solicitud.getEstado() != EstadoSolicitud.ACEPTADA) {
			throw new IllegalStateException("Solo puedes cancelar sesiones aceptadas.");
		}

		solicitud.setEstado(EstadoSolicitud.CANCELADA);
		solicitudDAO.actualizar(solicitud);
	}

	private List<SolicitudTutoria> filtrarProximasSesiones(List<SolicitudTutoria> sesiones) {
		LocalDate hoy = LocalDate.now();
		LocalTime ahora = LocalTime.now();
		return sesiones.stream()
				.filter(s -> {
					if (s.getFechaSesion().isAfter(hoy)) {
						return true;
					}
					if (s.getDisponibilidad() == null || s.getDisponibilidad().getHoraFin() == null) {
						return true;
					}
					try {
						return LocalTime.parse(s.getDisponibilidad().getHoraFin()).isAfter(ahora);
					} catch (RuntimeException e) {
						return true;
					}
				})
				.sorted((a, b) -> {
					int porFecha = a.getFechaSesion().compareTo(b.getFechaSesion());
					if (porFecha != 0) {
						return porFecha;
					}
					String ha = a.getDisponibilidad() != null ? a.getDisponibilidad().getHoraInicio() : "";
					String hb = b.getDisponibilidad() != null ? b.getDisponibilidad().getHoraInicio() : "";
					return ha.compareTo(hb);
				})
				.toList();
	}
}
