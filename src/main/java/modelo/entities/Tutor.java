package modelo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutor")
public class Tutor extends Usuario {

	private static final long serialVersionUID = 1L;

	@Column(length = 1000)
	private String materias;

	public Tutor() {
	}

	public Tutor(String email, String password, String nombre, String apellido) {
		super(email, password, nombre, apellido);
	}

	public String getMaterias() {
		return materias;
	}

	public void setMaterias(String materias) {
		this.materias = materias;
	}
}
