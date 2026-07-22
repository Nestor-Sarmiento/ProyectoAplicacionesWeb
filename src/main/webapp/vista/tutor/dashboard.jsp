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
            <c:if test="${not empty tutor.materias}">
                <p class="mt-3 text-sm text-outline inline-flex items-center gap-1">
                    <span class="material-symbols-outlined text-sm">menu_book</span>
                    <c:out value="${tutor.materias}"/>
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

            <a href="${pageContext.request.contextPath}/tutor?ruta=solicitudes"
               class="group block bg-surface-container-lowest rounded-xl p-8
                      shadow-[0_20px_40px_rgba(25,28,30,0.08)]
                      hover:ring-2 hover:ring-primary/20 transition-all">
                <div class="flex items-center gap-3 mb-4">
                    <span class="material-symbols-outlined text-secondary text-3xl">mail_outline</span>
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

        <section class="bg-surface-container-lowest rounded-xl p-8
                        shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
            <h2 class="text-xl font-extrabold text-on-surface mb-6 flex items-center gap-2"
                style="font-family:'Manrope',sans-serif">
                <span class="material-symbols-outlined text-primary">calendar_today</span>
                Próximas sesiones
            </h2>

            <c:choose>
                <c:when test="${not empty sesiones}">
                    <div class="space-y-4">
                        <c:forEach var="sesion" items="${sesiones}">
                            <div class="flex justify-between items-center p-4 border border-outline-variant/40 rounded-lg">
                                <div>
                                    <h3 class="font-bold text-on-surface"><c:out value="${sesion.nombreEstudiante}"/></h3>
                                    <p class="text-sm text-on-surface-variant"><c:out value="${sesion.tema}"/></p>
                                </div>
                                <div class="text-right">
                                    <p class="font-bold text-sm"><c:out value="${sesion.fecha}"/></p>
                                    <p class="text-xs text-outline"><c:out value="${sesion.duracion}"/></p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-on-surface-variant text-center py-8 text-sm">
                        No hay sesiones programadas próximamente.
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
