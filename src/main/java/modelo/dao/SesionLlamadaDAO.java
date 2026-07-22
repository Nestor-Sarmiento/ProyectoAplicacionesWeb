package modelo.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.entities.SesionLlamada;

public class SesionLlamadaDAO {

	public void guardar(SesionLlamada sesion) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(sesion);
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

	public Optional<SesionLlamada> buscarPorId(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return Optional.ofNullable(em.find(SesionLlamada.class, id));
		} finally {
			em.close();
		}
	}

	public Optional<SesionLlamada> buscarPorSolicitudId(Long solicitudId) {
		if (solicitudId == null) {
			return Optional.empty();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			List<SesionLlamada> resultados = em.createQuery(
					"SELECT s FROM SesionLlamada s WHERE s.solicitudId = :solicitudId "
							+ "ORDER BY s.id DESC",
					SesionLlamada.class)
					.setParameter("solicitudId", solicitudId)
					.setMaxResults(1)
					.getResultList();
			return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
		} finally {
			em.close();
		}
	}

	/** Última sesión de llamada por cada solicitud (si hay varias, gana el id mayor). */
	public Map<Long, SesionLlamada> mapearPorSolicitudIds(Collection<Long> solicitudIds) {
		if (solicitudIds == null || solicitudIds.isEmpty()) {
			return Collections.emptyMap();
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			List<SesionLlamada> sesiones = em.createQuery(
					"SELECT s FROM SesionLlamada s WHERE s.solicitudId IN :ids ORDER BY s.id DESC",
					SesionLlamada.class)
					.setParameter("ids", solicitudIds)
					.getResultList();
			Map<Long, SesionLlamada> mapa = new HashMap<>();
			for (SesionLlamada sesion : sesiones) {
				if (sesion.getSolicitudId() != null) {
					mapa.putIfAbsent(sesion.getSolicitudId(), sesion);
				}
			}
			return mapa;
		} finally {
			em.close();
		}
	}

	public void actualizar(SesionLlamada sesion) {
		if (sesion == null || sesion.getId() == null) {
			throw new IllegalArgumentException("La sesión de llamada es obligatoria.");
		}
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(sesion);
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
