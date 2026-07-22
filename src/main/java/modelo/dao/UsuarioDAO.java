package modelo.dao;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import modelo.conexion.JPAUtil;
import modelo.entities.Asignatura;
import modelo.entities.Carrera;
import modelo.entities.Estudiante;
import modelo.entities.Tutor;
import modelo.entities.Usuario;
import util.PasswordHasher;

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
			if (!PasswordHasher.coincide(password, usuario.getPassword())) {
				return null;
			}

			// Migrar contraseñas en texto plano al primer login correcto
			if (!PasswordHasher.esHash(usuario.getPassword())) {
				em.getTransaction().begin();
				usuario.setPassword(PasswordHasher.hash(password));
				em.merge(usuario);
				em.getTransaction().commit();
			}

			return usuario;
		} catch (NoResultException e) {
			return null;
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
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

	public Optional<Usuario> buscarPorId(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return Optional.ofNullable(em.find(Usuario.class, id));
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
		if (usuario == null) {
			throw new IllegalArgumentException("El usuario es obligatorio.");
		}
		if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
			throw new IllegalArgumentException("La contraseña es obligatoria.");
		}
		if (!PasswordHasher.esHash(usuario.getPassword())) {
			usuario.setPassword(PasswordHasher.hash(usuario.getPassword()));
		}

		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			if (usuario instanceof Estudiante estudiante && estudiante.getCarrera() != null) {
				estudiante.setCarrera(em.getReference(Carrera.class, estudiante.getCarrera().getId()));
			}

			if (usuario instanceof Tutor tutor) {
				if (tutor.getCarrera() != null) {
					tutor.setCarrera(em.getReference(Carrera.class, tutor.getCarrera().getId()));
				}
				if (tutor.getMaterias() != null && !tutor.getMaterias().isEmpty()) {
					Set<Asignatura> refs = new HashSet<>();
					for (Asignatura materia : tutor.getMaterias()) {
						refs.add(em.getReference(Asignatura.class, materia.getId()));
					}
					tutor.setMaterias(refs);
				}
			}

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
