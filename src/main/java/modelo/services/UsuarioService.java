package modelo.services;

import modelo.dao.UsuarioDAO;
import modelo.entities.Usuario;
import util.PasswordHasher;

public class UsuarioService {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	public Usuario autenticar(String email, String password) {
		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			return null;
		}

		Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
		if (usuario == null || !usuario.isActivo()) {
			return null;
		}
		if (!PasswordHasher.coincide(password, usuario.getPassword())) {
			return null;
		}

		if (!PasswordHasher.esHash(usuario.getPassword())) {
			usuario.setPassword(PasswordHasher.hash(password));
			usuarioDAO.actualizar(usuario);
		}

		return usuario;
	}

	public void registrar(Usuario usuario) {
		if (usuario == null) {
			throw new IllegalArgumentException("El usuario es obligatorio.");
		}
		if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
			throw new IllegalArgumentException("La contraseña es obligatoria.");
		}
		if (!PasswordHasher.esHash(usuario.getPassword())) {
			usuario.setPassword(PasswordHasher.hash(usuario.getPassword()));
		}
		usuarioDAO.guardar(usuario);
	}

	public boolean existeEmail(String email) {
		return usuarioDAO.buscarPorEmail(email) != null;
	}

	public long contar() {
		return usuarioDAO.contar();
	}
}
