package modelo.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelo.dao.TutorDAO;
import modelo.entities.Asignatura;
import modelo.entities.Tutor;

public class TutorService {

	private final TutorDAO tutorDAO = new TutorDAO();

	public Tutor buscarPorIdConMaterias(Long id) {
		return tutorDAO.buscarPorIdConMaterias(id);
	}

	public List<Tutor> buscar(Long carreraId, Long asignaturaId) {
		return tutorDAO.buscar(carreraId, asignaturaId);
	}

	public List<Tutor> buscar(Long carreraId, Long asignaturaId, Integer semestreMax) {
		return tutorDAO.buscar(carreraId, asignaturaId, semestreMax);
	}

	public void reemplazarMaterias(Long tutorId, Set<Long> asignaturaIds) {
		if (tutorId == null) {
			throw new IllegalArgumentException("Tutor obligatorio.");
		}

		Tutor tutor = tutorDAO.buscarPorIdConMaterias(tutorId);
		if (tutor == null) {
			throw new IllegalArgumentException("Tutor no encontrado.");
		}

		Set<Asignatura> nuevas = new HashSet<>();
		if (asignaturaIds != null && !asignaturaIds.isEmpty()) {
			List<Asignatura> encontradas = tutorDAO.buscarAsignaturasPorIds(asignaturaIds);
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
			throw new IllegalArgumentException("Debes seleccionar al menos una materia válida.");
		}

		tutor.getMaterias().clear();
		tutor.getMaterias().addAll(nuevas);
		tutorDAO.actualizar(tutor);
	}
}
