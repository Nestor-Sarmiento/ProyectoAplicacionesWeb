package modelo.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutor")
public class Tutor extends Usuario {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int semestre;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "carrera_id", nullable = false)
	private Carrera carrera;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "tutor_materia",
			joinColumns = @JoinColumn(name = "tutor_id"),
			inverseJoinColumns = @JoinColumn(name = "asignatura_id"))
	private Set<Asignatura> materias = new HashSet<>();

	public Tutor() {
		setRol(Rol.TUTOR);
	}

	public Tutor(String email, String password, String nombre, String apellido) {
		super(email, password, nombre, apellido);
		setRol(Rol.TUTOR);
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public Carrera getCarrera() {
		return carrera;
	}

	public void setCarrera(Carrera carrera) {
		this.carrera = carrera;
	}

	public Set<Asignatura> getMaterias() {
		return materias;
	}

	public void setMaterias(Set<Asignatura> materias) {
		this.materias = materias;
	}
}
