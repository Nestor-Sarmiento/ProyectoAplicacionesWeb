package controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.entities.Estudiante;
import modelo.entities.Usuario;

@WebServlet("/estudiante")
public class EstudianteController extends HttpServlet {

	private static final long serialVersionUID = 1L;

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
			case "inicio" -> inicio(req, resp);
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
		}
	}

	private void inicio(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Estudiante estudiante = requerirEstudiante(req, resp);
		if (estudiante == null) {
			return;
		}

		req.setAttribute("estudiante", estudiante);
		req.getRequestDispatcher("/vista/estudiante/dashboard.jsp").forward(req, resp);
	}

	private Estudiante requerirEstudiante(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession(false);
		Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");

		if (usuario == null) {
			resp.sendRedirect(req.getContextPath() + "/login?ruta=ingresar");
			return null;
		}

		if (!(usuario instanceof Estudiante estudiante)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso solo para estudiantes");
			return null;
		}

		return estudiante;
	}
}
