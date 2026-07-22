package modelo.entities;

public enum DiaSemana {
	LUNES("Lunes"),
	MARTES("Martes"),
	MIERCOLES("Miércoles"),
	JUEVES("Jueves"),
	VIERNES("Viernes"),
	SABADO("Sábado"),
	DOMINGO("Domingo");

	private final String etiqueta;

	DiaSemana(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public static DiaSemana parse(String valor) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException("Día de la semana obligatorio.");
		}
		return DiaSemana.valueOf(valor.trim().toUpperCase());
	}
}
