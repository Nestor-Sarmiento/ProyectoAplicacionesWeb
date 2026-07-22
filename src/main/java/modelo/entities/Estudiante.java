package modelo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiante")
public class Estudiante extends Usuario {

	private static final long serialVersionUID = 1L;

	@Column(length = 50)
	private String semestre;

	@Column(length = 100)
	private String carrera;

	public Estudiante() {
	}

	public Estudiante(String email, String password, String nombre, String apellido) {
		super(email, password, nombre, apellido);
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public String getCarrera() {
		return carrera;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}
}
