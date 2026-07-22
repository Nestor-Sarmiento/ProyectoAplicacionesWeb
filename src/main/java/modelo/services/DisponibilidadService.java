package modelo.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import modelo.dao.DisponibilidadDAO;
import modelo.entities.Disponibilidad;

public class DisponibilidadService {

	private final DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO();

	public List<Disponibilidad> listarPorTutor(Long tutorId) {
		return disponibilidadDAO.listarPorTutor(tutorId);
	}

	public String slotsComoCadena(Long tutorId) {
		return listarPorTutor(tutorId).stream()
				.map(Disponibilidad::toSlotKey)
				.collect(Collectors.joining(","));
	}

	public List<String> listarSlotKeys(Long tutorId) {
		return new ArrayList<>(listarPorTutor(tutorId).stream()
				.map(Disponibilidad::toSlotKey)
				.toList());
	}

	/**
	 * Reemplaza la disponibilidad visible del tutor (formato: LUNES-08:00,MARTES-09:00).
	 * Los horarios con solicitudes asociadas se desactivan en lugar de borrarse.
	 */
	public void reemplazarSlots(Long tutorId, String slotsCsv) {
		if (tutorId == null) {
			throw new IllegalArgumentException("Tutor obligatorio.");
		}
		disponibilidadDAO.sincronizarSlots(tutorId, parsearSlots(slotsCsv));
	}

	private Set<String> parsearSlots(String slotsCsv) {
		Set<String> slots = new LinkedHashSet<>();
		if (slotsCsv == null || slotsCsv.isBlank()) {
			return slots;
		}
		for (String parte : slotsCsv.split(",")) {
			String slot = parte.trim();
			if (!slot.isEmpty()) {
				if (!slot.matches("^[A-Z]+-\\d{2}:\\d{2}$")) {
					throw new IllegalArgumentException("Slot inválido: " + slot);
				}
				slots.add(slot);
			}
		}
		return slots;
	}
}
