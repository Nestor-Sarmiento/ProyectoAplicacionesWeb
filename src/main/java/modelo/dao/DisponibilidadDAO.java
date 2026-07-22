package modelo.dao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.entities.DiaSemana;
import modelo.entities.Disponibilidad;
import modelo.entities.Tutor;

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

	/**
	 * Sincroniza la disponibilidad del tutor con las claves {@code LUNES-08:00}.
	 * No borra filas referenciadas por solicitudes (las desactiva).
	 */
	public void sincronizarSlots(Long tutorId, Set<String> slotsDeseados) {
		if (tutorId == null) {
			throw new IllegalArgumentException("Tutor obligatorio.");
		}
		Set<String> pendientes = new LinkedHashSet<>(
				slotsDeseados == null ? Set.of() : slotsDeseados);

		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			Tutor tutor = em.getReference(Tutor.class, tutorId);
			List<Disponibilidad> existentes = em.createQuery(
					"SELECT d FROM Disponibilidad d WHERE d.tutor.id = :tutorId",
					Disponibilidad.class)
					.setParameter("tutorId", tutorId)
					.getResultList();

			for (Disponibilidad d : existentes) {
				String clave = d.toSlotKey();
				if (pendientes.contains(clave)) {
					d.setActivo(true);
					pendientes.remove(clave);
				} else if (estaReferenciada(em, d.getId())) {
					// Conserva la fila por las tutorías ya agendadas,
					// pero deja de ofrecerse a nuevos estudiantes.
					d.setActivo(false);
				} else {
					em.remove(d);
				}
			}

			for (String slot : pendientes) {
				Disponibilidad d = crearDesdeSlot(tutor, slot);
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

	private boolean estaReferenciada(EntityManager em, Long disponibilidadId) {
		Long count = em.createQuery(
				"SELECT COUNT(s.id) FROM SolicitudTutoria s WHERE s.disponibilidad.id = :id",
				Long.class)
				.setParameter("id", disponibilidadId)
				.getSingleResult();
		return count != null && count > 0;
	}

	private Disponibilidad crearDesdeSlot(Tutor tutor, String slot) {
		int sep = slot.lastIndexOf('-');
		if (sep <= 0 || sep >= slot.length() - 1) {
			throw new IllegalArgumentException("Slot inválido: " + slot);
		}

		DiaSemana dia = DiaSemana.parse(slot.substring(0, sep));
		String horaInicio = normalizarHora(slot.substring(sep + 1));
		String horaFin = sumarUnaHora(horaInicio);

		Disponibilidad d = new Disponibilidad();
		d.setTutor(tutor);
		d.setDiaSemana(dia);
		d.setHoraInicio(horaInicio);
		d.setHoraFin(horaFin);
		d.setActivo(true);
		return d;
	}

	private String normalizarHora(String hora) {
		if (hora == null || !hora.matches("^\\d{2}:\\d{2}$")) {
			throw new IllegalArgumentException("Hora inválida: " + hora);
		}
		return hora;
	}

	private String sumarUnaHora(String horaInicio) {
		int hora = Integer.parseInt(horaInicio.substring(0, 2));
		int siguiente = hora + 1;
		if (siguiente > 23) {
			throw new IllegalArgumentException("Hora fuera de rango: " + horaInicio);
		}
		return String.format("%02d:00", siguiente);
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
