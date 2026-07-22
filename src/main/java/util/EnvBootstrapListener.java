package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Asegura la carga de {@code .env} al arrancar la app en Tomcat/Eclipse.
 */
@WebListener
public class EnvBootstrapListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		EnvLoader.ensureLoaded();
		boolean ok = EnvLoader.get("CALLS_RENDER_URL") != null
				&& EnvLoader.get("CALLS_INTERNAL_KEY") != null
				&& EnvLoader.get("SMTP_HOST") != null;
		sce.getServletContext().log("EnvBootstrapListener: credenciales de llamada/correo "
				+ (ok ? "OK" : "INCOMPLETAS — revisa .env / OWLSHARE_ENV_FILE"));
	}
}
