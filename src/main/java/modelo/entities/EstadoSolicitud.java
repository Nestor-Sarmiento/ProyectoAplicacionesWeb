package modelo.entities;

public enum EstadoSolicitud {
	PENDIENTE("Pendiente"),
	ACEPTADA("Aceptada"),
	RECHAZADA("Rechazada"),
	CANCELADA("Cancelada");

	private final String etiqueta;

	EstadoSolicitud(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getEtiqueta() {
		return etiqueta;
	}
}
