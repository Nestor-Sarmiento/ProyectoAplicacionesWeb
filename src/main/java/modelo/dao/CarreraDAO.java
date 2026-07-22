package modelo.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import modelo.conexion.JPAUtil;
import modelo.entities.Carrera;

public class CarreraDAO {

	public List<Carrera> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery(
					"SELECT c FROM Carrera c ORDER BY c.nombre",
					Carrera.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	public Carrera buscarPorId(Long id) {
		if (id == null) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(Carrera.class, id);
		} finally {
			em.close();
		}
	}

	public Carrera buscarPorCodigo(String codigo) {
		if (codigo == null || codigo.isBlank()) {
			return null;
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			TypedQuery<Carrera> q = em.createQuery(
					"SELECT c FROM Carrera c WHERE c.codigo = :codigo",
					Carrera.class);
			q.setParameter("codigo", codigo.trim().toUpperCase());
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public long contar() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery("SELECT COUNT(c) FROM Carrera c", Long.class)
					.getSingleResult();
		} finally {
			em.close();
		}
	}

	public void guardar(Carrera carrera) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(carrera);
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
