package modelo.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import modelo.conexion.JPAUtil;
import modelo.entities.Asignatura;
import modelo.entities.Tutor;

public class TutorDAO {

	public Tutor buscarPorIdConMaterias(Long id) {
		if (id == null) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT DISTINCT t FROM Tutor t "
							+ "LEFT JOIN FETCH t.carrera "
							+ "LEFT JOIN FETCH t.materias "
							+ "WHERE t.id = :id",
					Tutor.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public void reemplazarMaterias(Long tutorId, Set<Long> asignaturaIds) {
		if (tutorId == null) {
			throw new IllegalArgumentException("Tutor obligatorio.");
		}

		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			Tutor tutor = em.createQuery(
					"SELECT t FROM Tutor t LEFT JOIN FETCH t.materias LEFT JOIN FETCH t.carrera WHERE t.id = :id",
					Tutor.class)
					.setParameter("id", tutorId)
					.getSingleResult();

			Set<Asignatura> nuevas = new HashSet<>();
			if (asignaturaIds != null && !asignaturaIds.isEmpty()) {
				List<Asignatura> encontradas = em.createQuery(
						"SELECT a FROM Asignatura a JOIN FETCH a.carrera WHERE a.id IN :ids",
						Asignatura.class)
						.setParameter("ids", asignaturaIds)
						.getResultList();

				for (Asignatura a : encontradas) {
					if (a.getCarrera() != null
							&& tutor.getCarrera() != null
							&& a.getCarrera().getId().equals(tutor.getCarrera().getId())
							&& a.getSemestre() < tutor.getSemestre()) {
						nuevas.add(a);
					}
				}
			}

			if (nuevas.isEmpty()) {
				em.getTransaction().rollback();
				throw new IllegalArgumentException("Debes seleccionar al menos una materia válida.");
			}

			tutor.getMaterias().clear();
			tutor.getMaterias().addAll(nuevas);
			em.merge(tutor);
			em.getTransaction().commit();
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public List<Tutor> buscar(Long carreraId, Long asignaturaId) {
		return buscar(carreraId, asignaturaId, null);
	}

	/**
	 * Busca tutores activos. Si {@code asignaturaId} está definido, filtra por esa materia.
	 * Si no, y {@code semestreMax} está definido, incluye tutores que dicten al menos
	 * una materia de la carrera con semestre &lt;= semestreMax.
	 */
	public List<Tutor> buscar(Long carreraId, Long asignaturaId, Integer semestreMax) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT DISTINCT t.id FROM Tutor t");
			boolean joinMaterias = asignaturaId != null
					|| (asignaturaId == null && semestreMax != null);
			if (joinMaterias) {
				jpql.append(" JOIN t.materias m");
			}
			jpql.append(" WHERE t.activo = true");
			if (carreraId != null) {
				jpql.append(" AND t.carrera.id = :carreraId");
			}
			if (asignaturaId != null) {
				jpql.append(" AND m.id = :asignaturaId");
			} else if (semestreMax != null) {
				jpql.append(" AND m.carrera.id = :carreraId AND m.semestre <= :semestreMax");
			}

			var query = em.createQuery(jpql.toString(), Long.class);
			if (carreraId != null) {
				query.setParameter("carreraId", carreraId);
			}
			if (asignaturaId != null) {
				query.setParameter("asignaturaId", asignaturaId);
			} else if (semestreMax != null) {
				query.setParameter("semestreMax", semestreMax);
			}

			List<Long> ids = query.getResultList();
			if (ids.isEmpty()) {
				return List.of();
			}

			return em.createQuery(
					"SELECT DISTINCT t FROM Tutor t "
							+ "LEFT JOIN FETCH t.carrera "
							+ "LEFT JOIN FETCH t.materias "
							+ "WHERE t.id IN :ids "
							+ "ORDER BY t.nombre, t.apellido",
					Tutor.class)
					.setParameter("ids", ids)
					.getResultList();
		} finally {
			em.close();
		}
	}
}
