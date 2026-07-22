<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Perfil del tutor</title>
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
            <a href="${pageContext.request.contextPath}/estudiante?ruta=buscar-tutor<c:if test='${not empty asignaturaIdSeleccionada}'>&asignaturaId=${asignaturaIdSeleccionada}</c:if>"
               class="inline-flex items-center text-outline hover:text-primary transition-colors"
               title="Volver a búsqueda">
                <span class="material-symbols-outlined">arrow_back</span>
            </a>
            <a href="${pageContext.request.contextPath}/estudiante?ruta=inicio"
               class="inline-flex items-center text-outline hover:text-primary transition-colors"
               title="Inicio">
                <span class="material-symbols-outlined">home</span>
            </a>
            <a href="${pageContext.request.contextPath}/login?ruta=logout"
               class="inline-flex items-center text-outline hover:text-red-600 transition-colors"
               title="Cerrar sesión">
                <span class="material-symbols-outlined">logout</span>
            </a>
        </div>
    </div>
</header>

<main class="flex-grow px-4 sm:px-6 py-10">
    <div class="max-w-4xl mx-auto space-y-6">

        <c:if test="${not empty mensaje}">
            <div class="flex items-center gap-3 bg-emerald-50 text-emerald-800 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">check_circle</span>
                <c:out value="${mensaje}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] overflow-hidden">
            <div class="bg-surface-container-low p-8">
                <div class="flex items-center gap-4">
                    <div class="w-16 h-16 rounded-full bg-primary text-on-primary
                                flex items-center justify-center font-bold text-2xl"
                         style="font-family:'Manrope',sans-serif">
                        <c:out value="${tutor.nombre.substring(0,1).toUpperCase()}"/>
                    </div>
                    <div>
                        <h1 class="text-2xl sm:text-3xl font-extrabold text-primary tracking-tight"
                            style="font-family:'Manrope',sans-serif">
                            <c:out value="${tutor.nombreCompleto}"/>
                        </h1>
                        <p class="text-on-surface-variant text-sm mt-1">
                            <c:out value="${tutor.carrera.nombre}"/>
                            · Semestre <c:out value="${tutor.semestre}"/>
                        </p>
                    </div>
                </div>
            </div>

            <div class="p-6 sm:p-8 space-y-8">
                <section>
                    <h2 class="text-sm font-bold uppercase tracking-wider text-primary mb-4">
                        Información de contacto
                    </h2>
                    <div class="bg-surface-container-low rounded-lg px-4 py-4 space-y-3">
                        <div class="flex items-center gap-3">
                            <span class="material-symbols-outlined text-primary text-base">mail</span>
                            <div>
                                <p class="text-[10px] font-bold text-outline uppercase tracking-wider">Correo institucional</p>
                                <a href="mailto:<c:out value='${tutor.email}'/>"
                                   class="text-sm font-semibold text-on-surface hover:text-primary">
                                    <c:out value="${tutor.email}"/>
                                </a>
                            </div>
                        </div>
                        <div class="flex items-center gap-3">
                            <span class="material-symbols-outlined text-primary text-base">badge</span>
                            <div>
                                <p class="text-[10px] font-bold text-outline uppercase tracking-wider">Nombre completo</p>
                                <p class="text-sm font-semibold text-on-surface">
                                    <c:out value="${tutor.nombreCompleto}"/>
                                </p>
                            </div>
                        </div>
                    </div>
                </section>

                <section>
                    <h2 class="text-sm font-bold uppercase tracking-wider text-primary mb-4">
                        Materias que ofrece
                    </h2>
                    <c:choose>
                        <c:when test="${not empty materiasTutor}">
                            <div class="space-y-3">
                                <c:set var="semActual" value="-1"/>
                                <c:forEach var="materia" items="${materiasTutor}">
                                    <c:if test="${materia.semestre != semActual}">
                                        <c:set var="semActual" value="${materia.semestre}"/>
                                        <p class="text-xs font-bold text-outline uppercase tracking-wider pt-1">
                                            Semestre <c:out value="${materia.semestre}"/>
                                        </p>
                                    </c:if>
                                    <div class="flex items-start gap-3 bg-surface-container-low rounded-lg px-4 py-3
                                                <c:if test='${not empty materiaBusqueda and materiaBusqueda.id == materia.id}'>ring-2 ring-primary/30</c:if>">
                                        <span class="material-symbols-outlined text-primary text-base mt-0.5">menu_book</span>
                                        <div>
                                            <p class="text-[10px] font-bold text-outline tracking-wide">
                                                <c:out value="${materia.codigo}"/>
                                            </p>
                                            <p class="text-sm font-semibold text-on-surface">
                                                <c:out value="${materia.nombre}"/>
                                            </p>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-sm text-outline text-center py-6">
                                Este tutor aún no ha publicado materias.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </section>

                <section>
                    <h2 class="text-sm font-bold uppercase tracking-wider text-primary mb-4">
                        Horario de disponibilidad
                    </h2>
                    <c:choose>
                        <c:when test="${not empty horariosPorDia}">
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <c:forEach var="entrada" items="${horariosPorDia}">
                                    <div class="bg-surface-container-low rounded-lg px-4 py-3">
                                        <p class="text-xs font-bold text-outline uppercase tracking-wider mb-2">
                                            <c:out value="${entrada.key.etiqueta}"/>
                                        </p>
                                        <div class="flex flex-wrap gap-2">
                                            <c:forEach var="slot" items="${entrada.value}">
                                                <span class="text-xs font-semibold bg-indigo-50 text-primary px-2.5 py-1 rounded">
                                                    <c:out value="${slot.horaInicio}"/> – <c:out value="${slot.horaFin}"/>
                                                </span>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-sm text-outline text-center py-6 bg-surface-container-low rounded-lg">
                                Este tutor aún no ha publicado horarios de disponibilidad.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </section>

                <section class="border-t border-outline-variant/30 pt-8">
                    <h2 class="text-sm font-bold uppercase tracking-wider text-primary mb-2">
                        Solicitar tutoría
                    </h2>
                    <p class="text-sm text-on-surface-variant mb-5">
                        Elige un horario de la semana en curso. Tu solicitud quedará pendiente hasta que el tutor responda.
                    </p>

                    <c:choose>
                        <c:when test="${empty materiasTutor}">
                            <p class="text-sm text-outline bg-surface-container-low rounded-lg px-4 py-4">
                                No puedes solicitar tutoría: el tutor no tiene materias publicadas.
                            </p>
                        </c:when>
                        <c:when test="${empty horariosTutor}">
                            <p class="text-sm text-outline bg-surface-container-low rounded-lg px-4 py-4">
                                No puedes solicitar tutoría: el tutor no tiene horarios disponibles.
                            </p>
                        </c:when>
                        <c:when test="${not puedeSolicitar}">
                            <p class="text-sm text-outline bg-surface-container-low rounded-lg px-4 py-4">
                                Este tutor no imparte materias habilitadas para tu semestre.
                            </p>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/estudiante?ruta=solicitar-tutoria&tutorId=${tutor.id}<c:if test='${not empty asignaturaIdSeleccionada}'>&asignaturaId=${asignaturaIdSeleccionada}</c:if>"
                               class="inline-flex items-center gap-2 px-6 py-3 rounded-lg
                                      bg-gradient-to-br from-primary to-primary-container text-on-primary
                                      font-bold text-sm shadow-md hover:opacity-90 transition-all"
                               style="font-family:'Manrope',sans-serif">
                                <span class="material-symbols-outlined text-base">event_available</span>
                                Solicitar tutoría
                            </a>
                        </c:otherwise>
                    </c:choose>
                </section>

                <a href="${pageContext.request.contextPath}/estudiante?ruta=buscar-tutor<c:if test='${not empty asignaturaIdSeleccionada}'>&asignaturaId=${asignaturaIdSeleccionada}</c:if>"
                   class="inline-flex items-center gap-2 text-sm font-bold text-primary hover:underline">
                    <span class="material-symbols-outlined text-base">arrow_back</span>
                    Volver a la búsqueda
                </a>
            </div>
        </div>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>
</body>
</html>
