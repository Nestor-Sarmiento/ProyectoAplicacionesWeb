package modelo.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(name = "primer_nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "segundo_nombre", length = 100)
	private String segundoNombre;

	@Column(name = "primer_apellido", nullable = false, length = 100)
	private String apellido;

	@Column(name = "segundo_apellido", length = 100)
	private String segundoApellido;

	@Column(nullable = false)
	private boolean activo = true;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Rol rol;

	protected Usuario() {
	}

	protected Usuario(String email, String password, String nombre, String apellido) {
		this.email = email;
		this.password = password;
		this.nombre = nombre;
		this.apellido = apellido;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSegundoNombre() {
		return segundoNombre;
	}

	public void setSegundoNombre(String segundoNombre) {
		this.segundoNombre = segundoNombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public String getNombreCompleto() {
		StringBuilder sb = new StringBuilder(nombre);
		if (segundoNombre != null && !segundoNombre.isBlank()) {
			sb.append(' ').append(segundoNombre.trim());
		}
		sb.append(' ').append(apellido);
		if (segundoApellido != null && !segundoApellido.isBlank()) {
			sb.append(' ').append(segundoApellido.trim());
		}
		return sb.toString();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}
}
