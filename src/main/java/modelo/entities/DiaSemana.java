package modelo.entities;

import java.time.DayOfWeek;

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

	public DayOfWeek toDayOfWeek() {
		return DayOfWeek.of(ordinal() + 1);
	}

	public static DiaSemana from(DayOfWeek dayOfWeek) {
		if (dayOfWeek == null) {
			throw new IllegalArgumentException("Día obligatorio.");
		}
		return DiaSemana.values()[dayOfWeek.getValue() - 1];
	}

	public static DiaSemana parse(String valor) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException("Día de la semana obligatorio.");
		}
		return DiaSemana.valueOf(valor.trim().toUpperCase());
	}
}
