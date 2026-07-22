package modelo.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.entities.Asignatura;

public class AsignaturaDAO {

	public List<Asignatura> listarPorCarrera(Long carreraId) {
		if (carreraId == null) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT a FROM Asignatura a WHERE a.carrera.id = :carreraId "
							+ "ORDER BY a.semestre, a.codigo",
					Asignatura.class)
					.setParameter("carreraId", carreraId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	public List<Asignatura> listarPorCarreraHastaSemestre(Long carreraId, int semestreMaximo) {
		if (carreraId == null || semestreMaximo < 1) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT a FROM Asignatura a WHERE a.carrera.id = :carreraId "
							+ "AND a.semestre <= :semestreMaximo ORDER BY a.semestre, a.codigo",
					Asignatura.class)
					.setParameter("carreraId", carreraId)
					.setParameter("semestreMaximo", semestreMaximo)
					.getResultList();
		} finally {
			em.close();
		}
	}

	public List<Asignatura> listarAprobadasParaTutor(Long carreraId, int semestreTutor) {
		if (carreraId == null || semestreTutor < 2) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT a FROM Asignatura a WHERE a.carrera.id = :carreraId "
							+ "AND a.semestre < :semestreTutor ORDER BY a.semestre, a.codigo",
					Asignatura.class)
					.setParameter("carreraId", carreraId)
					.setParameter("semestreTutor", semestreTutor)
					.getResultList();
		} finally {
			em.close();
		}
	}

	public Asignatura buscarPorId(Long id) {
		if (id == null) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(Asignatura.class, id);
		} finally {
			em.close();
		}
	}

	public List<Asignatura> buscarPorIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT DISTINCT a FROM Asignatura a JOIN FETCH a.carrera WHERE a.id IN :ids",
					Asignatura.class)
					.setParameter("ids", ids)
					.getResultList();
		} finally {
			em.close();
		}
	}
}
