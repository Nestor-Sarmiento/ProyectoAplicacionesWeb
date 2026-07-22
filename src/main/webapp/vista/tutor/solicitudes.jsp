<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Solicitudes</title>
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
            <a href="${pageContext.request.contextPath}/tutor?ruta=inicio"
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
    <div class="max-w-3xl mx-auto">
        <div class="mb-8">
            <h1 class="text-3xl sm:text-4xl font-extrabold text-primary tracking-tight mb-2"
                style="font-family:'Manrope',sans-serif">
                Solicitudes de tutoría
            </h1>
            <p class="text-on-surface-variant text-sm sm:text-base">
                Revisa las peticiones de estudiantes y acepta o rechaza según tu disponibilidad.
            </p>
        </div>

        <c:if test="${not empty mensaje}">
            <div class="mb-6 flex items-center gap-3 bg-emerald-50 text-emerald-800 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">check_circle</span>
                <c:out value="${mensaje}"/>
            </div>
        </c:if>
        <c:if test="${not empty aviso}">
            <div class="mb-6 flex items-center gap-3 bg-amber-50 text-amber-800 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">warning</span>
                <c:out value="${aviso}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="mb-6 flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty solicitudes}">
                <div class="space-y-4">
                    <c:forEach var="s" items="${solicitudes}">
                        <article class="bg-surface-container-lowest rounded-xl p-6
                                        shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
                            <div class="flex flex-wrap items-start justify-between gap-3 mb-4">
                                <div>
                                    <h2 class="font-bold text-on-surface text-lg">
                                        <c:out value="${s.estudiante.nombreCompleto}"/>
                                    </h2>
                                    <p class="text-xs text-outline mt-1">
                                        <c:out value="${s.estudiante.email}"/>
                                    </p>
                                </div>
                                <c:choose>
                                    <c:when test="${s.estado.name() == 'PENDIENTE'}">
                                        <span class="text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded bg-amber-50 text-amber-800">
                                            <c:out value="${s.estado.etiqueta}"/>
                                        </span>
                                    </c:when>
                                    <c:when test="${s.estado.name() == 'ACEPTADA'}">
                                        <span class="text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded bg-emerald-50 text-emerald-800">
                                            <c:out value="${s.estado.etiqueta}"/>
                                        </span>
                                    </c:when>
                                    <c:when test="${s.estado.name() == 'RECHAZADA'}">
                                        <span class="text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded bg-red-50 text-red-700">
                                            <c:out value="${s.estado.etiqueta}"/>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded bg-surface-container-highest text-outline">
                                            <c:out value="${s.estado.etiqueta}"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="space-y-2 text-sm mb-4">
                                <p>
                                    <span class="text-outline text-xs font-bold uppercase tracking-wider">Materia</span><br/>
                                    <span class="font-semibold">
                                        <c:out value="${s.asignatura.codigo}"/> — <c:out value="${s.asignatura.nombre}"/>
                                    </span>
                                </p>
                                <c:if test="${not empty s.disponibilidad}">
                                    <p>
                                        <span class="text-outline text-xs font-bold uppercase tracking-wider">Horario</span><br/>
                                        <span class="font-semibold">
                                            <c:if test="${not empty s.fechaSesion}">
                                                <c:out value="${s.fechaSesion}"/> ·
                                            </c:if>
                                            <c:out value="${s.disponibilidad.diaSemana.etiqueta}"/>
                                            · <c:out value="${s.disponibilidad.horaInicio}"/>
                                            – <c:out value="${s.disponibilidad.horaFin}"/>
                                        </span>
                                    </p>
                                </c:if>
                                <c:if test="${not empty s.mensaje}">
                                    <p>
                                        <span class="text-outline text-xs font-bold uppercase tracking-wider">Mensaje</span><br/>
                                        <span class="text-on-surface-variant"><c:out value="${s.mensaje}"/></span>
                                    </p>
                                </c:if>
                            </div>

                            <c:if test="${s.estado.name() == 'PENDIENTE'}">
                                <div class="flex flex-wrap gap-2 pt-2 border-t border-outline-variant/20">
                                    <form method="post" action="${pageContext.request.contextPath}/tutor">
                                        <input type="hidden" name="ruta" value="responder-solicitud"/>
                                        <input type="hidden" name="solicitudId" value="${s.id}"/>
                                        <input type="hidden" name="accion" value="aceptar"/>
                                        <button type="submit"
                                                class="inline-flex items-center gap-1 px-4 py-2 rounded-lg
                                                       bg-secondary text-white text-sm font-bold hover:opacity-90">
                                            <span class="material-symbols-outlined text-sm">check</span>
                                            Aceptar
                                        </button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/tutor">
                                        <input type="hidden" name="ruta" value="responder-solicitud"/>
                                        <input type="hidden" name="solicitudId" value="${s.id}"/>
                                        <input type="hidden" name="accion" value="rechazar"/>
                                        <button type="submit"
                                                class="inline-flex items-center gap-1 px-4 py-2 rounded-lg
                                                       bg-surface-container-highest text-on-surface-variant
                                                       text-sm font-bold hover:bg-red-50 hover:text-red-700">
                                            <span class="material-symbols-outlined text-sm">close</span>
                                            Rechazar
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                            <c:if test="${s.estado.name() == 'ACEPTADA'}">
                                <div class="flex flex-wrap gap-2 pt-2 border-t border-outline-variant/20">
                                    <c:if test="${not empty enlacesUnirse[s.id]}">
                                        <a href="${enlacesUnirse[s.id]}" target="_blank" rel="noopener noreferrer"
                                           class="inline-flex items-center gap-1 px-4 py-2 rounded-lg
                                                  bg-secondary text-white text-sm font-bold hover:opacity-90">
                                            <span class="material-symbols-outlined text-sm">videocam</span>
                                            Unirse a la tutoría
                                        </a>
                                    </c:if>
                                    <form method="post" action="${pageContext.request.contextPath}/tutor"
                                          onsubmit="return confirm('¿Seguro que deseas cancelar esta sesión?');">
                                        <input type="hidden" name="ruta" value="cancelar-solicitud"/>
                                        <input type="hidden" name="solicitudId" value="${s.id}"/>
                                        <button type="submit"
                                                class="inline-flex items-center gap-1 px-4 py-2 rounded-lg
                                                       bg-surface-container-highest text-on-surface-variant
                                                       text-sm font-bold hover:bg-red-50 hover:text-red-700">
                                            <span class="material-symbols-outlined text-sm">cancel</span>
                                            Cancelar sesión
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] p-12 text-center">
                    <span class="material-symbols-outlined text-4xl text-outline/40 block mb-4">inbox</span>
                    <p class="text-on-surface-variant">Aún no tienes solicitudes de tutoría.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>
</body>
</html>
