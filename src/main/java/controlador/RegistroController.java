package controlador;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.dao.AsignaturaDAO;
import modelo.dao.CarreraDAO;
import modelo.dao.UsuarioDAO;
import modelo.entities.Asignatura;
import modelo.entities.Carrera;
import modelo.entities.Estudiante;
import modelo.entities.Tutor;
import modelo.services.CatalogoSeeder;

@WebServlet("/registro")
public class RegistroController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CarreraDAO carreraDAO = new CarreraDAO();
	private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
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
			resp.sendRedirect(req.getContextPath() + "/registro?ruta=mostrar");
			return;
		}

		switch (ruta) {
			case "mostrar" -> mostrar(req, resp);
			case "registrar" -> registrar(req, resp);
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
		}
	}

	private void mostrar(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			CatalogoSeeder.asegurarCatalogo();
		} catch (RuntimeException e) {
			getServletContext().log("No se pudo cargar el catálogo académico", e);
			req.setAttribute("error", "No se pudo cargar el catálogo de carreras. Verifica la base de datos.");
		}

		List<Carrera> carreras = carreraDAO.listarTodas();
		req.setAttribute("carreras", carreras);
		req.setAttribute("catalogoJson", construirCatalogoJson(carreras));
		req.getRequestDispatcher("/vista/registro.jsp").forward(req, resp);
	}

	private void registrar(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		CatalogoSeeder.asegurarCatalogo();

		String tipo = trim(req.getParameter("tipo")).toUpperCase();
		String email = trim(req.getParameter("email"));
		String password = req.getParameter("password");
		String passwordConfirm = req.getParameter("passwordConfirm");
		String primerNombre = trim(req.getParameter("primerNombre"));
		String segundoNombre = trim(req.getParameter("segundoNombre"));
		String primerApellido = trim(req.getParameter("primerApellido"));
		String segundoApellido = trim(req.getParameter("segundoApellido"));
		String carreraIdRaw = trim(req.getParameter("carreraId"));
		String semestreRaw = trim(req.getParameter("semestre"));

		String error = validarBase(tipo, email, password, passwordConfirm,
				primerNombre, primerApellido, carreraIdRaw, semestreRaw);
		if (error != null) {
			req.setAttribute("error", error);
			mostrar(req, resp);
			return;
		}

		if (usuarioDAO.buscarPorEmail(email) != null) {
			req.setAttribute("error", "Ya existe una cuenta con ese correo.");
			mostrar(req, resp);
			return;
		}

		Long carreraId = Long.valueOf(carreraIdRaw);
		int semestre = Integer.parseInt(semestreRaw);
		Carrera carrera = carreraDAO.buscarPorId(carreraId);
		if (carrera == null) {
			req.setAttribute("error", "Carrera no válida.");
			mostrar(req, resp);
			return;
		}

		try {
			if ("ESTUDIANTE".equals(tipo)) {
				if (semestre < 1 || semestre > 8) {
					req.setAttribute("error", "El semestre del estudiante debe ser entre 1ro y 8vo.");
					mostrar(req, resp);
					return;
				}
				Estudiante estudiante = new Estudiante(email, password, primerNombre, primerApellido);
				estudiante.setSegundoNombre(blankToNull(segundoNombre));
				estudiante.setSegundoApellido(blankToNull(segundoApellido));
				estudiante.setSemestre(semestre);
				estudiante.setCarrera(carrera);
				usuarioDAO.guardar(estudiante);
			} else if ("TUTOR".equals(tipo)) {
				if (semestre < 2 || semestre > 9) {
					req.setAttribute("error", "El semestre del tutor debe ser entre 2do y 9no.");
					mostrar(req, resp);
					return;
				}

				String[] materiasParams = req.getParameterValues("materias");
				if (materiasParams == null || materiasParams.length == 0) {
					req.setAttribute("error", "Selecciona al menos una materia para dictar.");
					mostrar(req, resp);
					return;
				}

				Set<Asignatura> materias = resolverMateriasTutor(materiasParams, carreraId, semestre);
				if (materias.isEmpty()) {
					req.setAttribute("error", "Las materias seleccionadas no son válidas para tu semestre/carrera.");
					mostrar(req, resp);
					return;
				}

				Tutor tutor = new Tutor(email, password, primerNombre, primerApellido);
				tutor.setSegundoNombre(blankToNull(segundoNombre));
				tutor.setSegundoApellido(blankToNull(segundoApellido));
				tutor.setSemestre(semestre);
				tutor.setCarrera(carrera);
				tutor.setMaterias(materias);
				usuarioDAO.guardar(tutor);
			} else {
				req.setAttribute("error", "Tipo de cuenta no válido.");
				mostrar(req, resp);
				return;
			}
		} catch (RuntimeException e) {
			getServletContext().log("Error al registrar usuario", e);
			req.setAttribute("error", "No se pudo completar el registro. Intenta de nuevo.");
			mostrar(req, resp);
			return;
		}

		resp.sendRedirect(req.getContextPath()
				+ "/login?ruta=ingresar&mensaje="
				+ java.net.URLEncoder.encode("Cuenta creada. Ya puedes iniciar sesión.",
						java.nio.charset.StandardCharsets.UTF_8));
	}

	private Set<Asignatura> resolverMateriasTutor(String[] materiasParams, Long carreraId, int semestreTutor) {
		Set<Long> ids = new HashSet<>();
		for (String raw : materiasParams) {
			try {
				ids.add(Long.valueOf(raw));
			} catch (NumberFormatException ignored) {
				// skip invalid
			}
		}

		List<Asignatura> encontradas = asignaturaDAO.buscarPorIds(List.copyOf(ids));
		Set<Asignatura> validas = new HashSet<>();
		for (Asignatura a : encontradas) {
			if (a.getCarrera() != null
					&& carreraId.equals(a.getCarrera().getId())
					&& a.getSemestre() < semestreTutor) {
				validas.add(a);
			}
		}
		return validas;
	}

	private String validarBase(String tipo, String email, String password, String passwordConfirm,
			String primerNombre, String primerApellido, String carreraId, String semestre) {
		if (!"ESTUDIANTE".equals(tipo) && !"TUTOR".equals(tipo)) {
			return "Selecciona el tipo de cuenta.";
		}
		if (email.isEmpty()) {
			return "El correo es obligatorio.";
		}
		if (password == null || password.length() < 8) {
			return "La contraseña debe tener al menos 8 caracteres.";
		}
		if (!password.equals(passwordConfirm)) {
			return "Las contraseñas no coinciden.";
		}
		if (primerNombre.isEmpty() || primerApellido.isEmpty()) {
			return "Primer nombre y primer apellido son obligatorios.";
		}
		if (carreraId.isEmpty() || semestre.isEmpty()) {
			return "Carrera y semestre son obligatorios.";
		}
		try {
			Long.valueOf(carreraId);
			Integer.parseInt(semestre);
		} catch (NumberFormatException e) {
			return "Carrera o semestre no válidos.";
		}
		return null;
	}

	private String construirCatalogoJson(List<Carrera> carreras) {
		StringBuilder sb = new StringBuilder("{");
		boolean primeraCarrera = true;

		for (Carrera carrera : carreras) {
			if (!primeraCarrera) {
				sb.append(',');
			}
			primeraCarrera = false;
			sb.append('"').append(carrera.getId()).append("\":[");

			List<Asignatura> asignaturas = asignaturaDAO.listarPorCarrera(carrera.getId());
			boolean primera = true;
			for (Asignatura a : asignaturas) {
				if (!primera) {
					sb.append(',');
				}
				primera = false;
				sb.append("{\"id\":").append(a.getId())
						.append(",\"codigo\":\"").append(escaparJson(a.getCodigo())).append('"')
						.append(",\"nombre\":\"").append(escaparJson(a.getNombre())).append('"')
						.append(",\"semestre\":").append(a.getSemestre())
						.append('}');
			}
			sb.append(']');
		}

		sb.append('}');
		return sb.toString();
	}

	private String escaparJson(String valor) {
		if (valor == null) {
			return "";
		}
		return valor
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r");
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
