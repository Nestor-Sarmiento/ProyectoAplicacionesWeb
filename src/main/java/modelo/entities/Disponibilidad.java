package modelo.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
		name = "disponibilidad_tutor",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_tutor_dia_hora",
				columnNames = { "tutor_id", "dia_semana", "hora_inicio" }))
public class Disponibilidad implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tutor_id", nullable = false)
	private Tutor tutor;

	@Enumerated(EnumType.STRING)
	@Column(name = "dia_semana", nullable = false, length = 15)
	private DiaSemana diaSemana;

	@Column(name = "hora_inicio", nullable = false, length = 5)
	private String horaInicio;

	@Column(name = "hora_fin", nullable = false, length = 5)
	private String horaFin;

	@Column(nullable = false)
	private boolean activo = true;

	public Disponibilidad() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	public DiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/** Clave usada por el calendario: LUNES-08:00 */
	public String toSlotKey() {
		return diaSemana.name() + "-" + horaInicio;
	}
}
