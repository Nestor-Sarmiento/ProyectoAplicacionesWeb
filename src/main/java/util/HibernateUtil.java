package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/**
 * Utility class for managing Hibernate SessionFactory.
 * <p>
 * This class provides a singleton instance of SessionFactory, which is used to
 * create Hibernate sessions for database operations.
 */
public final class HibernateUtil {

    private static final String DEFAULT_SQLSERVER_URL = "jdbc:sqlserver://owlshare-server.database.windows.net:1433;database=db-owlshare;"
            + "encrypt=true;trustServerCertificate=false;loginTimeout=60;sslProtocol=TLSv1.2;";
    private static final String DEFAULT_SQLSERVER_USER = "sqladmin";

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        SessionFactory factory = sessionFactory;
        if (factory == null) {
            synchronized (HibernateUtil.class) {
                factory = sessionFactory;
                if (factory == null) {
                    sessionFactory = factory = buildSessionFactory();
                }
            }
        }
        return factory;
    }

    private static SessionFactory buildSessionFactory() {
        DbConfig config = loadDbConfig();

        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", config.url());
        configuration.setProperty("hibernate.connection.driver_class", config.driverClass());
        configuration.setProperty("hibernate.dialect", config.dialect());
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "false");

        // Da más tiempo a que la base "despierte" del auto-pause
        configuration.setProperty("hibernate.connection.provider_disables_autocommit", "true");

        if (config.username() != null) {
            configuration.setProperty("hibernate.connection.username", config.username());
        }
        if (config.password() != null) {
            configuration.setProperty("hibernate.connection.password", config.password());
        }

        return configuration.buildSessionFactory();
    }

    private static DbConfig loadDbConfig() {
        Properties properties = new Properties();
        loadPropertiesFile(properties, "database.local.properties");
        loadPropertiesFile(properties, Path.of("database.local.properties"));

        String url = firstNonBlank(
                System.getenv("DB_URL"),
                properties.getProperty("db.url"),
                DEFAULT_SQLSERVER_URL);

        if (!isSqlServerUrl(url)) {
            throw new IllegalStateException(
                    "La aplicación está configurada para usar SQL Server. " +
                            "Define DB_URL con un jdbc:sqlserver://... o deja la configuración por defecto.");
        }

        String username = firstNonBlank(
                System.getenv("DB_USER"),
                properties.getProperty("db.user"),
                DEFAULT_SQLSERVER_USER);
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), properties.getProperty("db.password"));
        if (password.isBlank()) {
            throw new IllegalStateException(
                    "Falta DB_PASSWORD. Defínelo como variable de entorno o en database.local.properties.");
        }

        return new DbConfig(
                url,
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "org.hibernate.dialect.SQLServerDialect",
                username,
                password);
    }

    private static boolean isSqlServerUrl(String url) {
        return url != null && url.toLowerCase(Locale.ROOT).startsWith("jdbc:sqlserver:");
    }

    private static void loadPropertiesFile(Properties target, String classpathResource) {
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (input != null) {
                target.load(input);
            }
        } catch (IOException ignored) {
        }
    }

    private static void loadPropertiesFile(Properties target, Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try (InputStream input = Files.newInputStream(path)) {
            target.load(input);
        } catch (IOException ignored) {
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private record DbConfig(String url, String driverClass, String dialect, String username, String password) {
    }
}
