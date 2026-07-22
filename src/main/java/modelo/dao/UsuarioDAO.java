package modelo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import modelo.conexion.JPAUtil;
import modelo.entities.Usuario;

public class UsuarioDAO {

	public Usuario autenticar(String email, String password) {
		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			return null;
		}

		EntityManager em = JPAUtil.getEntityManager();
		try {
			TypedQuery<Usuario> query = em.createQuery(
					"SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)",
					Usuario.class);
			query.setParameter("email", email.trim());

			Usuario usuario = query.getSingleResult();
			if (!usuario.isActivo()) {
				return null;
			}
			return password.equals(usuario.getPassword()) ? usuario : null;
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public Usuario buscarPorEmail(String email) {
		if (email == null || email.isBlank()) {
			return null;
		}

		EntityManager em = JPAUtil.getEntityManager();
		try {
			TypedQuery<Usuario> query = em.createQuery(
					"SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)",
					Usuario.class);
			query.setParameter("email", email.trim());
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public long contar() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class)
					.getSingleResult();
		} finally {
			em.close();
		}
	}

	public void guardar(Usuario usuario) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(usuario);
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
