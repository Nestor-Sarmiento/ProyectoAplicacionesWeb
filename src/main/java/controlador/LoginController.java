package controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.CarreraDAO;
import modelo.dao.UsuarioDAO;
import modelo.entities.Carrera;
import modelo.entities.Estudiante;
import modelo.entities.Tutor;
import modelo.entities.Usuario;
import modelo.services.CatalogoSeeder;
import util.EnvLoader;

@WebServlet("/login")
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CarreraDAO carreraDAO = new CarreraDAO();

	static {
		EnvLoader.ensureLoaded();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ruteador(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ruteador(req, resp);
	}

	private void ruteador(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String ruta = req.getParameter("ruta");

		if (ruta == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta no especificada");
			return;
		}

		switch (ruta) {
			case "ingresar" -> ingresar(req, resp);
			case "login" -> login(req, resp);
			case "logout" -> logout(req, resp);
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
		}
	}

	private void ingresar(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		asegurarUsuariosPrueba();
		req.getRequestDispatcher("/vista/login.jsp").forward(req, resp);
	}

	/** Datos de prueba si la BD está vacía (solo desarrollo). */
	private void asegurarUsuariosPrueba() {
		try {
			CatalogoSeeder.asegurarCatalogo();
			if (usuarioDAO.contar() > 0) {
				return;
			}

			Carrera software = carreraDAO.buscarPorCodigo("SOFTWARE");
			if (software == null) {
				return;
			}

			Estudiante estudiante = new Estudiante(
					"estudiante@epn.edu.ec", "12345678", "Ana", "Pérez");
			estudiante.setCarrera(software);
			estudiante.setSemestre(6);
			usuarioDAO.guardar(estudiante);

			Tutor tutor = new Tutor(
					"tutor@epn.edu.ec", "12345678", "Luis", "Gómez");
			tutor.setCarrera(software);
			tutor.setSemestre(5);
			usuarioDAO.guardar(tutor);
		} catch (RuntimeException e) {
			getServletContext().log("No se pudieron crear usuarios de prueba", e);
		}
	}

	private void login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = trim(req.getParameter("email"));
		String password = req.getParameter("password");

		if (email.isEmpty() || password == null || password.isBlank()) {
			req.setAttribute("error", "Debes ingresar correo electrónico y contraseña.");
			ingresar(req, resp);
			return;
		}

		Usuario usuario = usuarioDAO.autenticar(email, password);
		if (usuario == null) {
			req.setAttribute("error", "Credenciales inválidas.");
			ingresar(req, resp);
			return;
		}

		HttpSession session = req.getSession(true);
		session.setAttribute("usuario", usuario);

		if (usuario instanceof Estudiante) {
			resp.sendRedirect(req.getContextPath() + "/estudiante?ruta=inicio");
		} else if (usuario instanceof Tutor) {
			resp.sendRedirect(req.getContextPath() + "/tutor?ruta=inicio");
		} else {
			session.invalidate();
			req.setAttribute("error", "Tipo de usuario no soportado.");
			ingresar(req, resp);
		}
	}

	private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		resp.sendRedirect(req.getContextPath() + "/login?ruta=ingresar");
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
