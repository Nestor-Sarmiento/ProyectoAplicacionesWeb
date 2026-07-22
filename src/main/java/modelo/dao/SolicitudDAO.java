package modelo.dao;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import modelo.conexion.JPAUtil;
import modelo.entities.EstadoSolicitud;
import modelo.entities.SolicitudTutoria;

public class SolicitudDAO {

	public void guardar(SolicitudTutoria solicitud) {
		if (solicitud == null) {
			throw new IllegalArgumentException("La solicitud es obligatoria.");
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(solicitud);
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

	public SolicitudTutoria buscarPorId(Long id) {
		if (id == null) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(SolicitudTutoria.class, id);
		} finally {
			em.close();
		}
	}

	public SolicitudTutoria buscarPorIdConRelaciones(Long id) {
		if (id == null) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT s FROM SolicitudTutoria s "
							+ "JOIN FETCH s.estudiante "
							+ "JOIN FETCH s.tutor "
							+ "JOIN FETCH s.asignatura "
							+ "LEFT JOIN FETCH s.disponibilidad "
							+ "WHERE s.id = :id",
					SolicitudTutoria.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public void actualizar(SolicitudTutoria solicitud) {
		if (solicitud == null || solicitud.getId() == null) {
			throw new IllegalArgumentException("La solicitud es obligatoria.");
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(solicitud);
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

	public long contarActivasPorSlot(Long tutorId, Long disponibilidadId, LocalDate fecha) {
		if (tutorId == null || disponibilidadId == null || fecha == null) {
			return 0;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
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
			return count != null ? count : 0;
		} finally {
			em.close();
		}
	}

	public List<SolicitudTutoria> listarActivasPorTutorEnRango(
			Long tutorId, LocalDate inicioSemana, LocalDate finSemana) {
		if (tutorId == null || inicioSemana == null || finSemana == null) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
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
		} finally {
			em.close();
		}
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

	public List<SolicitudTutoria> listarAceptadasDesdeHoy(Long estudianteId, Long tutorId) {
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
			return query.getResultList();
		} finally {
			em.close();
		}
	}
}
