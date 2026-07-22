package modelo.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.entities.Disponibilidad;

public class DisponibilidadDAO {

	public Disponibilidad buscarPorId(Long id) {
		if (id == null) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(Disponibilidad.class, id);
		} finally {
			em.close();
		}
	}

	public List<Disponibilidad> listarPorTutor(Long tutorId) {
		if (tutorId == null) {
			return List.of();
		}

		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT d FROM Disponibilidad d WHERE d.tutor.id = :tutorId AND d.activo = true "
							+ "ORDER BY d.diaSemana, d.horaInicio",
					Disponibilidad.class)
					.setParameter("tutorId", tutorId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	public void eliminarPorTutor(Long tutorId) {
		if (tutorId == null) {
			throw new IllegalArgumentException("Tutor obligatorio.");
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Disponibilidad d WHERE d.tutor.id = :tutorId")
					.setParameter("tutorId", tutorId)
					.executeUpdate();
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

	public void guardar(Disponibilidad disponibilidad) {
		if (disponibilidad == null) {
			throw new IllegalArgumentException("Disponibilidad obligatoria.");
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(disponibilidad);
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

	public void guardarTodos(List<Disponibilidad> disponibilidades) {
		if (disponibilidades == null || disponibilidades.isEmpty()) {
			return;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			for (Disponibilidad d : disponibilidades) {
				em.persist(d);
			}
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
