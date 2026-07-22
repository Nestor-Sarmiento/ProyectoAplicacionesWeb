package modelo.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "asignatura",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_carrera_codigo",
				columnNames = { "carrera_id", "codigo" }))
public class Asignatura implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String codigo;

	@Column(nullable = false, length = 200)
	private String nombre;

	@Column(nullable = false)
	private int semestre;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "carrera_id", nullable = false)
	private Carrera carrera;

	public Asignatura() {
	}

	public Asignatura(String codigo, String nombre, int semestre) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.semestre = semestre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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
