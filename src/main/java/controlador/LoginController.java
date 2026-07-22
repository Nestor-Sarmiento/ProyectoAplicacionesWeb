package controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.UsuarioDAO;
import modelo.entities.Estudiante;
import modelo.entities.Tutor;
import modelo.entities.Usuario;

@WebServlet("/login")
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

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
			case "home" -> home(req, resp);
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
			if (usuarioDAO.contar() > 0) {
				return;
			}
			Estudiante estudiante = new Estudiante(
					"estudiante@epn.edu.ec", "12345678", "Ana", "Pérez");
			estudiante.setCarrera("Ingeniería en Sistemas");
			estudiante.setSemestre("Sexto");
			usuarioDAO.guardar(estudiante);

			Tutor tutor = new Tutor(
					"tutor@epn.edu.ec", "12345678", "Luis", "Gómez");
			tutor.setMaterias("Cálculo, Programación");
			usuarioDAO.guardar(tutor);
		} catch (RuntimeException e) {
			// Si la BD no está lista, el login mostrará error al autenticar.
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

		resp.sendRedirect(req.getContextPath() + "/login?ruta=home");
	}

	private void home(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");

		if (usuario == null) {
			resp.sendRedirect(req.getContextPath() + "/login?ruta=ingresar");
			return;
		}

		req.setAttribute("usuario", usuario);

		if (usuario instanceof Estudiante) {
			req.setAttribute("tipoUsuario", "Estudiante");
		} else if (usuario instanceof Tutor) {
			req.setAttribute("tipoUsuario", "Tutor");
		} else {
			req.setAttribute("tipoUsuario", "Usuario");
		}

		req.getRequestDispatcher("/vista/home.jsp").forward(req, resp);
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
