package util;

/**
 * Compatibilidad: el proyecto usa EclipseLink vía {@link modelo.conexion.JPAUtil}.
 * Esta clase queda deprecada para evitar mezclar Hibernate.
 */
@Deprecated
public final class HibernateUtil {

	private HibernateUtil() {
	}

	public static Object getSessionFactory() {
		throw new UnsupportedOperationException(
				"HibernateUtil no se usa en OwlShare. Usa modelo.conexion.JPAUtil.");
	}
}
