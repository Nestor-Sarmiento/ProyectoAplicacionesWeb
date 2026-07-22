package modelo.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.entities.DiaSemana;
import modelo.entities.Disponibilidad;
import modelo.entities.Tutor;

public class DisponibilidadDAO {

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

	public String slotsComoCadena(Long tutorId) {
		return listarPorTutor(tutorId).stream()
				.map(Disponibilidad::toSlotKey)
				.collect(Collectors.joining(","));
	}

	/**
	 * Reemplaza toda la disponibilidad del tutor con los slots del calendario
	 * (formato: LUNES-08:00,MARTES-09:00).
	 */
	public void reemplazarSlots(Long tutorId, String slotsCsv) {
		if (tutorId == null) {
			throw new IllegalArgumentException("Tutor obligatorio.");
		}

		Set<String> slots = parsearSlots(slotsCsv);

		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			em.createQuery("DELETE FROM Disponibilidad d WHERE d.tutor.id = :tutorId")
					.setParameter("tutorId", tutorId)
					.executeUpdate();

			Tutor tutor = em.getReference(Tutor.class, tutorId);
			for (String slot : slots) {
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

	private Set<String> parsearSlots(String slotsCsv) {
		Set<String> slots = new LinkedHashSet<>();
		if (slotsCsv == null || slotsCsv.isBlank()) {
			return slots;
		}
		for (String parte : slotsCsv.split(",")) {
			String slot = parte.trim();
			if (!slot.isEmpty()) {
				slots.add(slot);
			}
		}
		return slots;
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

	public List<String> listarSlotKeys(Long tutorId) {
		return new ArrayList<>(listarPorTutor(tutorId).stream()
				.map(Disponibilidad::toSlotKey)
				.toList());
	}
}
