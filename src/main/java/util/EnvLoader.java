package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Carga variables desde {@code .env} y las combina con variables de entorno / system properties.
 * <p>
 * En Eclipse "Run on Server" el cwd suele ser la carpeta temporal de Tomcat (wtpwebapps),
 * no la raíz del proyecto; por eso también busca por ruta explícita, classpath y
 * carpetas típicas bajo el home del usuario.
 */
public final class EnvLoader {

	private static final Map<String, String> ENV_FILE = new HashMap<>();
	private static volatile boolean loaded = false;

	private EnvLoader() {
	}

	public static synchronized void ensureLoaded() {
		if (loaded) {
			return;
		}

		Path env = localizarEnv();
		if (env != null) {
			System.out.println("EnvLoader: cargando archivo " + env.toAbsolutePath());
			cargarArchivo(env);
		}

		if (ENV_FILE.isEmpty()) {
			if (cargarDesdeClasspath("/.env") || cargarDesdeClasspath("/owlshare.env")) {
				System.out.println("EnvLoader: cargado desde classpath");
			}
		}

		if (ENV_FILE.isEmpty()) {
			System.err.println("EnvLoader: no se encontró .env. cwd="
					+ Paths.get("").toAbsolutePath()
					+ " | Define OWLSHARE_ENV_FILE con la ruta absoluta al .env"
					+ " o coloca el archivo en la raíz del proyecto OwlShare.");
		} else {
			System.out.println("EnvLoader: claves=" + ENV_FILE.keySet());
		}
		loaded = true;
	}

	public static String get(String name) {
		ensureLoaded();
		String fromFile = ENV_FILE.get(name);
		if (fromFile != null && !fromFile.isBlank()) {
			return fromFile.trim();
		}
		String fromSystem = System.getenv(name);
		if (fromSystem != null && !fromSystem.isBlank()) {
			return fromSystem.trim();
		}
		String fromProp = System.getProperty(name);
		if (fromProp != null && !fromProp.isBlank()) {
			return fromProp.trim();
		}
		return null;
	}

	public static String getOrDefault(String name, String defaultValue) {
		String value = get(name);
		return value == null || value.isBlank() ? defaultValue : value;
	}

	private static Path localizarEnv() {
		Path override = rutaOverride();
		if (override != null) {
			return override;
		}

		Path encontrado = buscarHaciaArriba(Paths.get("").toAbsolutePath().normalize());
		if (encontrado != null) {
			return encontrado;
		}

		encontrado = buscarDesdeClasspathLocation();
		if (encontrado != null) {
			return encontrado;
		}

		String catalina = System.getProperty("catalina.base");
		if (catalina != null) {
			Path candidate = Paths.get(catalina).resolve(".env");
			if (Files.isRegularFile(candidate)) {
				return candidate;
			}
		}

		return buscarEnHomeUsuario();
	}

	private static Path rutaOverride() {
		String raw = System.getProperty("owlshare.env.file");
		if (raw == null || raw.isBlank()) {
			raw = System.getenv("OWLSHARE_ENV_FILE");
		}
		if (raw == null || raw.isBlank()) {
			return null;
		}
		Path path = Paths.get(raw.trim());
		return Files.isRegularFile(path) ? path.toAbsolutePath().normalize() : null;
	}

	private static Path buscarDesdeClasspathLocation() {
		try {
			var codeSource = EnvLoader.class.getProtectionDomain().getCodeSource();
			if (codeSource == null || codeSource.getLocation() == null) {
				return null;
			}
			Path classes = Paths.get(codeSource.getLocation().toURI()).toAbsolutePath().normalize();
			if (Files.isRegularFile(classes)) {
				classes = classes.getParent();
			}
			return buscarHaciaArriba(classes);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Eclipse WTP no deja el .env junto al war desplegado; buscamos la carpeta
	 * del proyecto OwlShare bajo el home del usuario.
	 */
	private static Path buscarEnHomeUsuario() {
		String home = System.getProperty("user.home");
		if (home == null || home.isBlank()) {
			return null;
		}
		Path homePath = Paths.get(home);

		Path[] candidatosDirectos = {
				homePath.resolve("Desktop/EPN/Web/OwlShare/.env"),
				homePath.resolve("Desktop/OwlShare/.env"),
				homePath.resolve("Documents/OwlShare/.env"),
				homePath.resolve("Documents/EPN/Web/OwlShare/.env"),
				homePath.resolve("OneDrive/Desktop/EPN/Web/OwlShare/.env"),
				homePath.resolve("OneDrive/Escritorio/EPN/Web/OwlShare/.env"),
				homePath.resolve("Escritorio/EPN/Web/OwlShare/.env")
		};
		for (Path candidate : candidatosDirectos) {
			if (Files.isRegularFile(candidate)) {
				return candidate.toAbsolutePath().normalize();
			}
		}

		return buscarOwlShareEnv(homePath, 5);
	}

	private static Path buscarOwlShareEnv(Path root, int maxDepth) {
		if (!Files.isDirectory(root)) {
			return null;
		}
		Queue<Path> queue = new ArrayDeque<>();
		Queue<Integer> depths = new ArrayDeque<>();
		Set<Path> vistos = new HashSet<>();
		queue.add(root);
		depths.add(0);

		while (!queue.isEmpty()) {
			Path dir = queue.poll();
			int depth = depths.poll();
			if (dir == null || !vistos.add(dir) || depth > maxDepth) {
				continue;
			}

			Path env = dir.resolve(".env");
			if (Files.isRegularFile(env) && "OwlShare".equalsIgnoreCase(dir.getFileName().toString())) {
				return env.toAbsolutePath().normalize();
			}

			if (depth == maxDepth) {
				continue;
			}
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
				for (Path child : stream) {
					String name = child.getFileName().toString();
					if (name.startsWith(".") || name.equalsIgnoreCase("node_modules")
							|| name.equalsIgnoreCase("AppData") || name.equalsIgnoreCase("target")
							|| name.equalsIgnoreCase("bin") || name.equalsIgnoreCase("build")) {
						continue;
					}
					if (Files.isDirectory(child)) {
						queue.add(child);
						depths.add(depth + 1);
					}
				}
			} catch (IOException ignored) {
				// sin permiso o enlace roto: seguir
			}
		}
		return null;
	}

	private static Path buscarHaciaArriba(Path start) {
		Path dir = start;
		for (int i = 0; i < 10 && dir != null; i++) {
			Path candidate = dir.resolve(".env");
			if (Files.isRegularFile(candidate)) {
				return candidate;
			}
			dir = dir.getParent();
		}
		return null;
	}

	private static boolean cargarDesdeClasspath(String resource) {
		try (InputStream in = EnvLoader.class.getResourceAsStream(resource)) {
			if (in == null) {
				return false;
			}
			cargarStream(in);
			return !ENV_FILE.isEmpty();
		} catch (IOException e) {
			System.err.println("EnvLoader: no se pudo leer " + resource + " del classpath: " + e.getMessage());
			return false;
		}
	}

	private static void cargarArchivo(Path env) {
		try (BufferedReader reader = Files.newBufferedReader(env, StandardCharsets.UTF_8)) {
			cargarReader(reader);
		} catch (IOException e) {
			System.err.println("No se pudo leer .env en " + env + ": " + e.getMessage());
		}
	}

	private static void cargarStream(InputStream in) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			cargarReader(reader);
		}
	}

	private static void cargarReader(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			int eq = line.indexOf('=');
			if (eq <= 0) {
				continue;
			}
			String key = line.substring(0, eq).trim().replace("\uFEFF", "");
			String value = line.substring(eq + 1).trim();
			if ((value.startsWith("\"") && value.endsWith("\""))
					|| (value.startsWith("'") && value.endsWith("'"))) {
				value = value.substring(1, value.length() - 1);
			}
			ENV_FILE.put(key, value);
		}
	}
}
