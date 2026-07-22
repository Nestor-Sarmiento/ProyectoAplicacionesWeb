<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Inicio tutor</title>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&family=Manrope:wght@700;800&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: {
                "surface": "#f7f9fc", "surface-container": "#eceef1",
                "surface-container-low": "#f2f4f7", "surface-container-highest": "#e0e3e6",
                "surface-container-lowest": "#ffffff", "on-surface": "#191c1e",
                "on-surface-variant": "#454652", "primary": "#24389c",
                "primary-container": "#3f51b5", "on-primary": "#ffffff",
                "secondary": "#006a60", "outline": "#757684", "outline-variant": "#c5c5d4"
            }}}
        }
    </script>
</head>
<body class="bg-surface text-on-surface min-h-screen flex flex-col">

<header class="bg-white/80 backdrop-blur-xl sticky top-0 z-50 shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
    <div class="flex justify-between items-center w-full px-8 py-4 max-w-screen-2xl mx-auto">
        <span class="text-2xl font-extrabold text-indigo-900 tracking-tighter"
              style="font-family:'Manrope',sans-serif">OwlShare</span>
        <div class="flex items-center gap-4">
            <span class="text-sm text-on-surface-variant hidden sm:inline">
                Hola, <strong class="text-on-surface"><c:out value="${tutor.nombre}"/></strong>
            </span>
            <a href="${pageContext.request.contextPath}/login?ruta=logout"
               class="inline-flex items-center gap-1 text-outline hover:text-red-600 transition-colors"
               title="Cerrar sesión">
                <span class="material-symbols-outlined">logout</span>
            </a>
        </div>
    </div>
</header>

<main class="flex-grow px-6 py-10">
    <div class="max-w-4xl mx-auto">
        <div class="mb-10">
            <h1 class="text-4xl font-extrabold text-primary tracking-tight mb-2"
                style="font-family:'Manrope',sans-serif">
                Panel del tutor
            </h1>
            <p class="text-on-surface-variant text-base">
                Bienvenido, <c:out value="${tutor.nombre}"/>. Gestiona tus horarios y solicitudes de tutoría.
            </p>
            <c:if test="${not empty tutor.carrera}">
                <p class="mt-3 text-sm text-outline">
                    <span class="inline-flex items-center gap-1 mr-3">
                        <span class="material-symbols-outlined text-sm">school</span>
                        <c:out value="${tutor.carrera.nombre}"/>
                    </span>
                    <span class="inline-flex items-center gap-1">
                        <span class="material-symbols-outlined text-sm">calendar_month</span>
                        Semestre <c:out value="${tutor.semestre}"/>
                    </span>
                </p>
            </c:if>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-10">
            <a href="${pageContext.request.contextPath}/tutor?ruta=horarios"
               class="group block bg-surface-container-lowest rounded-xl p-8
                      shadow-[0_20px_40px_rgba(25,28,30,0.08)]
                      hover:ring-2 hover:ring-primary/20 transition-all">
                <div class="flex items-center gap-3 mb-4">
                    <span class="material-symbols-outlined text-primary text-3xl">calendar_month</span>
                    <h2 class="text-2xl font-extrabold text-primary"
                        style="font-family:'Manrope',sans-serif">Horarios</h2>
                </div>
                <p class="text-on-surface-variant text-sm mb-6">
                    Define tu disponibilidad semanal y revisa tu agenda.
                </p>
                <span class="inline-flex items-center gap-2 text-primary font-bold text-sm group-hover:gap-3 transition-all">
                    Gestionar horarios
                    <span class="material-symbols-outlined text-base">arrow_forward</span>
                </span>
            </a>

            <a href="${pageContext.request.contextPath}/tutor?ruta=materias"
               class="group block bg-surface-container-lowest rounded-xl p-8
                      shadow-[0_20px_40px_rgba(25,28,30,0.08)]
                      hover:ring-2 hover:ring-primary/20 transition-all">
                <div class="flex items-center gap-3 mb-4">
                    <span class="material-symbols-outlined text-secondary text-3xl">menu_book</span>
                    <h2 class="text-2xl font-extrabold text-primary"
                        style="font-family:'Manrope',sans-serif">Materias</h2>
                </div>
                <p class="text-on-surface-variant text-sm mb-6">
                    Elige o actualiza las materias en las que dictas tutorías.
                </p>
                <span class="inline-flex items-center gap-2 text-primary font-bold text-sm group-hover:gap-3 transition-all">
                    Editar materias
                    <span class="material-symbols-outlined text-base">arrow_forward</span>
                </span>
            </a>

            <a href="${pageContext.request.contextPath}/tutor?ruta=solicitudes"
               class="group block bg-surface-container-lowest rounded-xl p-8
                      shadow-[0_20px_40px_rgba(25,28,30,0.08)]
                      hover:ring-2 hover:ring-primary/20 transition-all md:col-span-2">
                <div class="flex items-center gap-3 mb-4">
                    <span class="material-symbols-outlined text-primary text-3xl">mail_outline</span>
                    <h2 class="text-2xl font-extrabold text-primary"
                        style="font-family:'Manrope',sans-serif">Solicitudes</h2>
                </div>
                <p class="text-on-surface-variant text-sm mb-6">
                    Revisa y responde las solicitudes de tutoría de estudiantes.
                </p>
                <span class="inline-flex items-center gap-2 text-primary font-bold text-sm group-hover:gap-3 transition-all">
                    Ver solicitudes
                    <span class="material-symbols-outlined text-base">arrow_forward</span>
                </span>
            </a>
        </div>

        <section class="bg-surface-container-lowest rounded-xl p-8 mb-10
                        shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
                <h2 class="text-xl font-extrabold text-on-surface flex items-center gap-2"
                    style="font-family:'Manrope',sans-serif">
                    <span class="material-symbols-outlined text-primary">auto_stories</span>
                    Mis materias
                </h2>
                <a href="${pageContext.request.contextPath}/tutor?ruta=materias"
                   class="text-sm font-bold text-primary hover:underline inline-flex items-center gap-1">
                    Editar
                    <span class="material-symbols-outlined text-sm">edit</span>
                </a>
            </div>

            <c:choose>
                <c:when test="${not empty materiasTutor}">
                    <div class="flex flex-wrap gap-2">
                        <c:forEach var="materia" items="${materiasTutor}">
                            <span class="inline-flex flex-col bg-surface-container-low rounded-lg px-3 py-2">
                                <span class="text-[10px] font-bold uppercase tracking-wide text-outline">
                                    <c:out value="${materia.codigo}"/>
                                </span>
                                <span class="text-xs font-semibold text-on-surface">
                                    <c:out value="${materia.nombre}"/>
                                </span>
                            </span>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-on-surface-variant text-sm text-center py-4">
                        Aún no has seleccionado materias.
                        <a href="${pageContext.request.contextPath}/tutor?ruta=materias"
                           class="text-primary font-bold hover:underline">Agrégalas aquí</a>.
                    </p>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="bg-surface-container-lowest rounded-xl p-8
                        shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
                <h2 class="text-xl font-extrabold text-on-surface flex items-center gap-2"
                    style="font-family:'Manrope',sans-serif">
                    <span class="material-symbols-outlined text-primary">calendar_today</span>
                    Próximas sesiones
                </h2>
                <a href="${pageContext.request.contextPath}/tutor?ruta=solicitudes"
                   class="text-sm font-bold text-primary hover:underline inline-flex items-center gap-1">
                    Ver solicitudes
                    <span class="material-symbols-outlined text-sm">arrow_forward</span>
                </a>
            </div>

            <c:choose>
                <c:when test="${not empty proximasSesiones}">
                    <div class="space-y-4">
                        <c:forEach var="sesion" items="${proximasSesiones}">
                            <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3
                                        p-4 border border-outline-variant/40 rounded-lg">
                                <div>
                                    <h3 class="font-bold text-on-surface">
                                        <c:out value="${sesion.estudiante.nombreCompleto}"/>
                                    </h3>
                                    <p class="text-sm text-on-surface-variant">
                                        <c:out value="${sesion.asignatura.codigo}"/>
                                        — <c:out value="${sesion.asignatura.nombre}"/>
                                    </p>
                                </div>
                                <div class="sm:text-right">
                                    <p class="font-bold text-sm text-on-surface">
                                        <c:out value="${sesion.fechaSesion}"/>
                                    </p>
                                    <c:if test="${not empty sesion.disponibilidad}">
                                        <p class="text-xs text-outline">
                                            <c:out value="${sesion.disponibilidad.diaSemana.etiqueta}"/>
                                            · <c:out value="${sesion.disponibilidad.horaInicio}"/>
                                            – <c:out value="${sesion.disponibilidad.horaFin}"/>
                                        </p>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-on-surface-variant text-center py-8 text-sm">
                        No hay sesiones programadas próximamente.
                        Cuando aceptes una solicitud, aparecerá aquí.
                    </p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>
</body>
</html>
