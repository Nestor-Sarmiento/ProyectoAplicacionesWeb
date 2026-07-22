package modelo.services;

import jakarta.persistence.EntityManager;
import modelo.conexion.JPAUtil;
import modelo.dao.CarreraDAO;
import modelo.entities.Asignatura;
import modelo.entities.Carrera;

/**
 * Carga inicial de carreras y asignaturas (solo si el catálogo está vacío).
 */
public final class CatalogoSeeder {

	private static final CarreraDAO carreraDAO = new CarreraDAO();

	private CatalogoSeeder() {
	}

	/**
	 * EclipseLink create-tables no recrea tablas borradas a mano mientras el EMF
	 * ya está vivo; aseguramos la de solicitudes con SQL idempotente.
	 */
	private static void asegurarTablaSolicitudes() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.createNativeQuery("""
					CREATE TABLE IF NOT EXISTS solicitud_tutoria (
					  ID BIGINT NOT NULL AUTO_INCREMENT,
					  ESTADO VARCHAR(20) NOT NULL,
					  fecha_creacion DATETIME NOT NULL,
					  fecha_sesion DATE NOT NULL,
					  MENSAJE VARCHAR(500) NOT NULL,
					  asignatura_id BIGINT NOT NULL,
					  disponibilidad_id BIGINT NOT NULL,
					  estudiante_id BIGINT NOT NULL,
					  tutor_id BIGINT NOT NULL,
					  PRIMARY KEY (ID),
					  CONSTRAINT FK_sol_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignatura (ID),
					  CONSTRAINT FK_sol_disponibilidad FOREIGN KEY (disponibilidad_id) REFERENCES disponibilidad_tutor (ID),
					  CONSTRAINT FK_sol_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante (ID),
					  CONSTRAINT FK_sol_tutor FOREIGN KEY (tutor_id) REFERENCES tutor (ID)
					)
					""").executeUpdate();
			em.getTransaction().commit();
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			// Si ya existe con otro nombre de constraint, no bloquear el arranque.
			System.err.println("Aviso al asegurar solicitud_tutoria: " + e.getMessage());
		} finally {
			em.close();
		}
	}

	private static void asegurarTablaSesionLlamada() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.createNativeQuery("""
					CREATE TABLE IF NOT EXISTS sesion_llamada (
					  ID BIGINT NOT NULL AUTO_INCREMENT,
					  tutor_id BIGINT NOT NULL,
					  student_id BIGINT NOT NULL,
					  solicitud_id BIGINT NULL,
					  room_name VARCHAR(255) NULL,
					  livekit_url VARCHAR(500) NULL,
					  tutor_token VARCHAR(2000) NULL,
					  student_token VARCHAR(2000) NULL,
					  calificacion INT NULL,
					  comentario VARCHAR(1000) NULL,
					  completada TINYINT(1) NOT NULL DEFAULT 0,
					  PRIMARY KEY (ID)
					)
					""").executeUpdate();
			em.getTransaction().commit();
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			System.err.println("Aviso al asegurar sesion_llamada: " + e.getMessage());
		} finally {
			em.close();
		}
	}

	public static synchronized void asegurarCatalogo() {
		asegurarTablaSolicitudes();
		asegurarTablaSesionLlamada();

		if (carreraDAO.contar() > 0) {
			return;
		}

		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			Carrera software = nuevaCarrera(em, "SOFTWARE", "Software");
			cargarSoftware(software);

			Carrera computacion = nuevaCarrera(em, "COMPUTACION", "Computación");
			cargarComputacion(computacion);

			Carrera cienciaDatos = nuevaCarrera(em, "CIENCIA_DATOS_IA",
					"Ciencia de Datos e Inteligencia Artificial");
			cargarCienciaDatos(cienciaDatos);

			Carrera sistemas = nuevaCarrera(em, "SISTEMAS_INFORMACION", "Sistemas de Información");
			cargarSistemasInformacion(sistemas);

			em.getTransaction().commit();
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	private static Carrera nuevaCarrera(EntityManager em, String codigo, String nombre) {
		Carrera c = new Carrera(codigo, nombre);
		em.persist(c);
		return c;
	}

	private static void mat(Carrera carrera, String codigo, String nombre, int semestre) {
		carrera.agregarAsignatura(new Asignatura(codigo, nombre, semestre));
	}

	private static void cargarSoftware(Carrera c) {
		mat(c, "MATD113", "Álgebra Lineal", 1);
		mat(c, "MATD123", "Cálculo en una Variable", 1);
		mat(c, "FISD134", "Mecánica Newtoniana", 1);
		mat(c, "ICCD144", "Programación I", 1);
		mat(c, "CSHD111", "Comunicación Oral y Escrita", 1);
		mat(c, "MATD213", "Ecuaciones Diferenciales Ordinarias", 2);
		mat(c, "ICCD224", "Matemáticas Computacionales y Teoría de la Computación", 2);
		mat(c, "ICCD233", "Fundamentos de Electrónica para Computación", 2);
		mat(c, "ICCD244", "Programación II", 2);
		mat(c, "CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2);
		mat(c, "MATD223", "Probabilidad y Estadísticas Básicas", 3);
		mat(c, "ICCD323", "Sistemas Operativos", 3);
		mat(c, "ICCD332", "Arquitectura de Computadores", 3);
		mat(c, "ICCD343", "Estructura de Datos y Algoritmos I", 3);
		mat(c, "ICCD353", "Fundamentos de Redes y Conectividad", 3);
		mat(c, "CSHD300", "Asignatura de Artes y Humanidades", 3);
		mat(c, "ISWD414", "Ingeniería de Software y Requerimientos", 4);
		mat(c, "ICCD422", "Compiladores y Lenguajes", 4);
		mat(c, "ISWD433", "Fundamentos de Sistemas de Información", 4);
		mat(c, "ICCD442", "Estructura de Datos y Algoritmos II", 4);
		mat(c, "ISWD453", "Fundamentos de Bases de Datos", 4);
		mat(c, "CDHD400", "Asignatura de Economía y Sociedad", 4);
		mat(c, "ADMD511", "Gestión Organizacional", 5);
		mat(c, "ISWD523", "Diseño de Software", 5);
		mat(c, "ICCD533", "Computación Gráfica", 5);
		mat(c, "ISWD543", "Inteligencia Artificial y Aprendizaje Automático", 5);
		mat(c, "ISWD553", "Bases de Datos Distribuidas", 5);
		mat(c, "ISWD613", "Aplicaciones Web", 6);
		mat(c, "ISWD622", "Metodologías Ágiles", 6);
		mat(c, "ISWD633", "Construcción y Evolución de Software", 6);
		mat(c, "ISWD643", "Tecnologías de Seguridad", 6);
		mat(c, "ISWD652", "Calidad del Software", 6);
		mat(c, "ADMD611", "Gestión de Procesos y Calidad", 6);
		mat(c, "ADMD711", "Ingeniería Financiera", 6);
		mat(c, "ISWD713", "Aplicaciones Móviles", 7);
		mat(c, "ISWD723", "Interacción Humano-Computador", 7);
		mat(c, "ISWD732", "Usabilidad y Accesibilidad", 7);
		mat(c, "ISWD743", "Business Intelligence", 7);
		mat(c, "ISWD752", "Verificación y Validación de Software", 7);
		mat(c, "ISWD762", "Automatización de Procesos", 7);
		mat(c, "ISWD813", "Aplicaciones Web Avanzadas", 8);
		mat(c, "ISWD823", "Desarrollo de Juegos Interactivos", 8);
		mat(c, "ICCD833", "Auditoría Informática", 8);
		mat(c, "ICCD842", "Profesionalismo en Informática", 8);
		mat(c, "ISWD853", "Desarrollo de Software Seguro", 8);
	}

	private static void cargarComputacion(Carrera c) {
		mat(c, "MATD113", "Álgebra Lineal", 1);
		mat(c, "MATD123", "Cálculo en una Variable", 1);
		mat(c, "FISD134", "Mecánica Newtoniana", 1);
		mat(c, "ICCD144", "Programación I", 1);
		mat(c, "CSHD111", "Comunicación Oral y Escrita", 1);
		mat(c, "MATD213", "Ecuaciones Diferenciales Ordinarias", 2);
		mat(c, "ICCD224", "Matemáticas Computacionales y Teoría de la Computación", 2);
		mat(c, "ICCD233", "Fundamentos de Electrónica para Computación", 2);
		mat(c, "ICCD244", "Programación II", 2);
		mat(c, "CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2);
		mat(c, "MATD223", "Probabilidad y Estadísticas Básicas", 3);
		mat(c, "ICCD323", "Sistemas Operativos", 3);
		mat(c, "ICCD332", "Arquitectura de Computadores", 3);
		mat(c, "ICCD343", "Estructura de Datos y Algoritmos I", 3);
		mat(c, "ICCD353", "Fundamentos de Redes y Conectividad", 3);
		mat(c, "CSHD300", "Asignatura de Artes y Humanidades", 3);
		mat(c, "ICCD412", "Métodos Numéricos", 4);
		mat(c, "ICCD422", "Compiladores y Lenguajes", 4);
		mat(c, "ICCD432", "Multiprocesamiento y Arquitecturas Alternativas", 4);
		mat(c, "ICCD442", "Estructura de Datos y Algoritmos II", 4);
		mat(c, "ICCD453", "Fundamentos de Bases de Datos", 4);
		mat(c, "ICCD463", "Redes de Computadoras I", 4);
		mat(c, "CDHD400", "Asignatura de Economía y Sociedad", 4);
		mat(c, "ICCD512", "Ingeniería de Software I", 5);
		mat(c, "ICCD523", "Inteligencia Artificial", 5);
		mat(c, "ICCD533", "Computación Gráfica", 5);
		mat(c, "ADMD511", "Gestión Organizacional", 5);
		mat(c, "ISWD553", "Bases de Datos Distribuidas", 5);
		mat(c, "ICCD563", "Redes de Computadoras II", 5);
		mat(c, "ISWD613", "Aplicaciones Web", 6);
		mat(c, "ICCD623", "Data Mining y Machine Learning", 6);
		mat(c, "ICCD632", "Ingeniería de Software II", 6);
		mat(c, "ISWD643", "Tecnologías de Seguridad", 6);
		mat(c, "ICCD654", "Computación Distribuida", 6);
		mat(c, "ISWD713", "Aplicaciones Móviles", 7);
		mat(c, "ISWD723", "Interacción Humano-Computador", 7);
		mat(c, "ICCD733", "Seguridad Informática", 7);
		mat(c, "ISWD743", "Business Intelligence", 7);
		mat(c, "ICCD753", "Recuperación de la Información", 7);
		mat(c, "ICCD814", "Modelos y Simulación", 8);
		mat(c, "ICCD823", "Cloud Computing", 8);
		mat(c, "ICCD833", "Auditoría Informática", 8);
		mat(c, "ICCD842", "Profesionalismo en Informática", 8);
		mat(c, "ADMD711", "Ingeniería Financiera", 8);
		mat(c, "ADMD611", "Gestión de Procesos y Calidad", 8);
	}

	private static void cargarCienciaDatos(Carrera c) {
		mat(c, "MATD113", "Álgebra Lineal", 1);
		mat(c, "MATD123", "Cálculo de una Variable", 1);
		mat(c, "FISD134", "Mecánica Newtoniana", 1);
		mat(c, "ICCD144", "Programación I", 1);
		mat(c, "CSHD111", "Comunicación Oral y Escrita", 1);
		mat(c, "MATD213", "Ecuaciones Diferenciales Ordinarias", 2);
		mat(c, "ISID223", "Introducción a los Sistemas de Información", 2);
		mat(c, "ISID232", "Fundamentos de Ciencias de la Computación", 2);
		mat(c, "ICCD244", "Programación II", 2);
		mat(c, "ICCD332", "Arquitectura de Computadores", 2);
		mat(c, "CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2);
		mat(c, "ICCD353", "Fundamentos de Redes y Conectividad", 3);
		mat(c, "MATD223", "Probabilidad y Estadísticas Básicas", 3);
		mat(c, "ICCD323", "Sistemas Operativos", 3);
		mat(c, "ICCD343", "Estructura de Datos y Algoritmos I", 3);
		mat(c, "ISWD453", "Fundamentos de Bases de Datos", 3);
		mat(c, "ISID413", "Fundamentos de Big Data", 4);
		mat(c, "IDSD422", "Estadística y Programación para Ciencias de Datos I", 4);
		mat(c, "ICCD422", "Compiladores y Lenguajes", 4);
		mat(c, "ICCD442", "Estructura de Datos y Algoritmos II", 4);
		mat(c, "ISWD553", "Bases de Datos Distribuidas", 4);
		mat(c, "CSHD300", "Asignatura de Artes y Humanidades", 4);
		mat(c, "PSCD202", "Prácticas de Servicio Comunitario", 4);
		mat(c, "IDSD513", "Infraestructura para Big Data", 5);
		mat(c, "IDSD522", "Estadística y Programación para Ciencias de Datos II", 5);
		mat(c, "IDSD533", "Computación Numérica", 5);
		mat(c, "ICCD523", "Inteligencia Artificial", 5);
		mat(c, "ISID432", "Desarrollo y Mantenimiento de Software", 5);
		mat(c, "CSHD400", "Asignatura de Economía y Sociedad", 5);
		mat(c, "ADMD511", "Gestión Organizacional", 5);
		mat(c, "IDSD613", "Almacenamiento de Datos Masivos", 6);
		mat(c, "IDSD623", "Computación Paralela para Big Data", 6);
		mat(c, "IDSD633", "Aprendizaje Automático I", 6);
		mat(c, "ISWD613", "Aplicaciones Web", 6);
		mat(c, "ICCD643", "Tecnologías de Seguridad", 6);
		mat(c, "IDSD713", "Procesamiento de Datos Masivos", 7);
		mat(c, "IDSD723", "Aprendizaje Automático II", 7);
		mat(c, "IDSD733", "Minería de Datos I", 7);
		mat(c, "IDSD742", "Seguridad y Privacidad de Datos", 7);
		mat(c, "ISID642", "User Experience", 7);
		mat(c, "ADMD611", "Gestión de Procesos y Calidad", 7);
		mat(c, "ADMD711", "Ingeniería Financiera", 7);
		mat(c, "IDSD813", "Visualización de Datos", 8);
		mat(c, "IDSD823", "Servicios en la Nube para Big Data", 8);
		mat(c, "IDSD833", "Minería de Datos II", 8);
		mat(c, "IDSD843", "Analítica Avanzada", 8);
		mat(c, "ICCD842", "Profesionalismo en Informática", 8);
	}

	private static void cargarSistemasInformacion(Carrera c) {
		mat(c, "MATD113", "Álgebra Lineal", 1);
		mat(c, "MATD123", "Cálculo de una Variable", 1);
		mat(c, "FISD134", "Mecánica Newtoniana", 1);
		mat(c, "ICCD144", "Programación I", 1);
		mat(c, "CSHD111", "Comunicación Oral y Escrita", 1);
		mat(c, "MATD213", "Ecuaciones Diferenciales Ordinarias", 2);
		mat(c, "ISID223", "Introducción a los Sistemas de Información", 2);
		mat(c, "ISID232", "Fundamentos de Ciencias de la Computación", 2);
		mat(c, "ICCD244", "Programación II", 2);
		mat(c, "ICCD332", "Arquitectura de Computadores", 2);
		mat(c, "CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2);
		mat(c, "ICCD353", "Fundamentos de Redes y Conectividad", 3);
		mat(c, "MATD223", "Probabilidad y Estadísticas Básicas", 3);
		mat(c, "ICCD323", "Sistemas Operativos", 3);
		mat(c, "ICCD343", "Estructura de Datos y Algoritmos I", 3);
		mat(c, "ISWD453", "Fundamentos de Bases de Datos", 3);
		mat(c, "ISID413", "Administración de la Información y Datos", 4);
		mat(c, "ISID423", "Análisis y Diseño de Sistemas de Información", 4);
		mat(c, "ISID432", "Desarrollo y Mantenimiento de Software", 4);
		mat(c, "ICCD442", "Estructura de Datos y Algoritmos II", 4);
		mat(c, "ISWD553", "Bases de Datos Distribuidas", 4);
		mat(c, "CSHD300", "Asignatura de Artes y Humanidades", 4);
		mat(c, "CSHD400", "Asignatura de Economía y Sociedad", 4);
		mat(c, "ICCD533", "Computación Gráfica", 5);
		mat(c, "ISWD732", "Usabilidad y Accesibilidad", 5);
		mat(c, "ISID533", "Arquitectura Empresarial", 5);
		mat(c, "ISWD723", "Interacción Humano-Computador", 5);
		mat(c, "ISID553", "Infraestructura de Tecnologías de Información", 5);
		mat(c, "ADMD511", "Gestión Organizacional", 5);
		mat(c, "ICCD753", "Recuperación de Información", 6);
		mat(c, "ISID623", "Automatización de Procesos de Negocio", 6);
		mat(c, "ISID633", "Gestión del Conocimiento", 6);
		mat(c, "ISID642", "User Experience", 6);
		mat(c, "ISID653", "Fundamentos de Ciencia de Datos", 6);
		mat(c, "ADMD611", "Gestión de Procesos y Calidad", 6);
		mat(c, "ICCD523", "Inteligencia Artificial", 7);
		mat(c, "ISID723", "Analítica Predictiva", 7);
		mat(c, "ICCD733", "Seguridad Informática", 7);
		mat(c, "ISID743", "Gobernanza y Calidad de Datos", 7);
		mat(c, "ISID752", "Liderazgo y Comunicación", 7);
		mat(c, "ADMD711", "Ingeniería Financiera", 7);
		mat(c, "ICCD823", "Cloud Computing", 8);
		mat(c, "ISWD743", "Business Intelligence", 8);
		mat(c, "ICCD833", "Auditoría Informática", 8);
		mat(c, "ISID843", "Gestión de Proyectos de Sistemas de Información", 8);
		mat(c, "ISID852", "Internet of Things", 8);
	}
}
