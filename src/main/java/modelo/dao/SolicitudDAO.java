package modelo.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.entities.Asignatura;
import modelo.entities.DiaSemana;
import modelo.entities.Disponibilidad;
import modelo.entities.EstadoSolicitud;
import modelo.entities.Estudiante;
import modelo.entities.SolicitudTutoria;
import modelo.entities.Tutor;

public class SolicitudDAO {

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

		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			Estudiante estudiante = em.find(Estudiante.class, estudianteId);
			Asignatura asignatura = em.find(Asignatura.class, asignaturaId);

			Tutor tutor;
			try {
				tutor = em.createQuery(
						"SELECT DISTINCT t FROM Tutor t LEFT JOIN FETCH t.materias WHERE t.id = :id",
						Tutor.class)
						.setParameter("id", tutorId)
						.getSingleResult();
			} catch (jakarta.persistence.NoResultException e) {
				tutor = null;
			}

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
				throw new IllegalArgumentException(
						"Esa materia no está habilitada para tu semestre.");
			}

			Disponibilidad disponibilidad = em.find(Disponibilidad.class, disponibilidadId);
			if (disponibilidad == null || !disponibilidad.isActivo()) {
				throw new IllegalArgumentException("Horario de disponibilidad inválido.");
			}
			if (!disponibilidad.getTutor().getId().equals(tutorId)) {
				throw new IllegalArgumentException("Horario de disponibilidad inválido.");
			}

			DiaSemana diaEsperado = DiaSemana.from(fechaSesion.getDayOfWeek());
			if (disponibilidad.getDiaSemana() != diaEsperado) {
				throw new IllegalArgumentException(
						"La fecha no corresponde al día del horario seleccionado.");
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

			if (slotOcupado(em, tutorId, disponibilidadId, fechaSesion)) {
				throw new IllegalArgumentException(
						"Ese horario ya fue solicitado por otro estudiante.");
			}

			SolicitudTutoria solicitud = new SolicitudTutoria();
			solicitud.setEstudiante(estudiante);
			solicitud.setTutor(tutor);
			solicitud.setAsignatura(asignatura);
			solicitud.setDisponibilidad(disponibilidad);
			solicitud.setFechaSesion(fechaSesion);
			solicitud.setMensaje(mensajeLimpio);
			solicitud.setEstado(EstadoSolicitud.PENDIENTE);

			em.persist(solicitud);
			em.getTransaction().commit();
			return solicitud;
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	/**
	 * Claves ocupadas en la semana: {@code disponibilidadId|yyyy-MM-dd}.
	 */
	public Set<String> clavesSlotsOcupados(Long tutorId, LocalDate inicioSemana, LocalDate finSemana) {
		if (tutorId == null || inicioSemana == null || finSemana == null) {
			return Set.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			List<SolicitudTutoria> activas = em.createQuery(
					"SELECT s FROM SolicitudTutoria s "
							+ "JOIN FETCH s.disponibilidad "
							+ "WHERE s.tutor.id = :tutorId "
							+ "AND s.estado IN :estados "
							+ "AND s.fechaSesion BETWEEN :inicio AND :fin",
					SolicitudTutoria.class)
					.setParameter("tutorId", tutorId)
					.setParameter("estados", List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA))
					.setParameter("inicio", inicioSemana)
					.setParameter("fin", finSemana)
					.getResultList();

			Set<String> claves = new HashSet<>();
			for (SolicitudTutoria s : activas) {
				claves.add(claveSlot(s.getDisponibilidad().getId(), s.getFechaSesion()));
			}
			return claves;
		} finally {
			em.close();
		}
	}

	public static String claveSlot(Long disponibilidadId, LocalDate fecha) {
		return disponibilidadId + "|" + fecha;
	}

	private boolean slotOcupado(EntityManager em, Long tutorId, Long disponibilidadId, LocalDate fecha) {
		Long count = em.createQuery(
				"SELECT COUNT(s.id) FROM SolicitudTutoria s "
						+ "WHERE s.tutor.id = :tutorId "
						+ "AND s.disponibilidad.id = :disponibilidadId "
						+ "AND s.fechaSesion = :fecha "
						+ "AND s.estado IN :estados",
				Long.class)
				.setParameter("tutorId", tutorId)
				.setParameter("disponibilidadId", disponibilidadId)
				.setParameter("fecha", fecha)
				.setParameter("estados", List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA))
				.getSingleResult();
		return count != null && count > 0;
	}

	public List<SolicitudTutoria> listarPorTutor(Long tutorId) {
		if (tutorId == null) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT DISTINCT s FROM SolicitudTutoria s "
							+ "JOIN FETCH s.estudiante "
							+ "JOIN FETCH s.asignatura "
							+ "LEFT JOIN FETCH s.disponibilidad "
							+ "WHERE s.tutor.id = :tutorId "
							+ "ORDER BY s.fechaCreacion DESC",
					SolicitudTutoria.class)
					.setParameter("tutorId", tutorId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	public List<SolicitudTutoria> listarPorEstudiante(Long estudianteId) {
		if (estudianteId == null) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT DISTINCT s FROM SolicitudTutoria s "
							+ "JOIN FETCH s.tutor "
							+ "JOIN FETCH s.asignatura "
							+ "LEFT JOIN FETCH s.disponibilidad "
							+ "WHERE s.estudiante.id = :estudianteId "
							+ "ORDER BY s.fechaCreacion DESC",
					SolicitudTutoria.class)
					.setParameter("estudianteId", estudianteId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	/** Sesiones aceptadas con fecha de hoy en adelante, ordenadas por fecha y hora. */
	public List<SolicitudTutoria> listarProximasSesionesEstudiante(Long estudianteId) {
		return listarProximasSesiones(estudianteId, null);
	}

	public List<SolicitudTutoria> listarProximasSesionesTutor(Long tutorId) {
		return listarProximasSesiones(null, tutorId);
	}

	private List<SolicitudTutoria> listarProximasSesiones(Long estudianteId, Long tutorId) {
		if (estudianteId == null && tutorId == null) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			String jpql = "SELECT DISTINCT s FROM SolicitudTutoria s "
					+ "JOIN FETCH s.estudiante "
					+ "JOIN FETCH s.tutor "
					+ "JOIN FETCH s.asignatura "
					+ "LEFT JOIN FETCH s.disponibilidad "
					+ "WHERE s.estado = :estado "
					+ "AND s.fechaSesion >= :hoy ";
			if (estudianteId != null) {
				jpql += "AND s.estudiante.id = :estudianteId ";
			}
			if (tutorId != null) {
				jpql += "AND s.tutor.id = :tutorId ";
			}
			jpql += "ORDER BY s.fechaSesion ASC";

			var query = em.createQuery(jpql, SolicitudTutoria.class)
					.setParameter("estado", EstadoSolicitud.ACEPTADA)
					.setParameter("hoy", LocalDate.now());
			if (estudianteId != null) {
				query.setParameter("estudianteId", estudianteId);
			}
			if (tutorId != null) {
				query.setParameter("tutorId", tutorId);
			}

			List<SolicitudTutoria> sesiones = query.getResultList();
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
		} finally {
			em.close();
		}
	}

	public void actualizarEstado(Long solicitudId, Long tutorId, EstadoSolicitud nuevoEstado) {
		if (solicitudId == null || tutorId == null || nuevoEstado == null) {
			throw new IllegalArgumentException("Datos incompletos.");
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			SolicitudTutoria s = em.find(SolicitudTutoria.class, solicitudId);
			if (s == null || !s.getTutor().getId().equals(tutorId)) {
				throw new IllegalArgumentException("Solicitud no encontrada.");
			}
			if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
				throw new IllegalStateException("Solo se pueden responder solicitudes pendientes.");
			}
			s.setEstado(nuevoEstado);
			em.getTransaction().commit();
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}
}
