package modelo.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitud_tutoria")
public class SolicitudTutoria implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "estudiante_id", nullable = false)
	private Estudiante estudiante;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tutor_id", nullable = false)
	private Tutor tutor;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "disponibilidad_id", nullable = false)
	private Disponibilidad disponibilidad;

	@Column(name = "fecha_sesion", nullable = false)
	private LocalDate fechaSesion;

	@Column(nullable = false, length = 500)
	private String mensaje;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;

	public SolicitudTutoria() {
	}

	@PrePersist
	protected void onCreate() {
		if (fechaCreacion == null) {
			fechaCreacion = LocalDateTime.now();
		}
		if (estado == null) {
			estado = EstadoSolicitud.PENDIENTE;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Estudiante getEstudiante() {
		return estudiante;
	}

	public void setEstudiante(Estudiante estudiante) {
		this.estudiante = estudiante;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	public Disponibilidad getDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(Disponibilidad disponibilidad) {
		this.disponibilidad = disponibilidad;
	}

	public LocalDate getFechaSesion() {
		return fechaSesion;
	}

	public void setFechaSesion(LocalDate fechaSesion) {
		this.fechaSesion = fechaSesion;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}
