package modelo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiante")
public class Estudiante extends Usuario {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int semestre;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "carrera_id", nullable = false)
	private Carrera carrera;

	public Estudiante() {
	}

	public Estudiante(String email, String password, String nombre, String apellido) {
		super(email, password, nombre, apellido);
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
}
