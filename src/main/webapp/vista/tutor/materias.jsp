<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Materias del tutor</title>
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
    <style>
        .materia-check:checked + label {
            background-color: #24389c;
            color: #fff;
            border-color: #24389c;
        }
    </style>
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
            <a href="${pageContext.request.contextPath}/tutor?ruta=inicio"
               class="inline-flex items-center text-outline hover:text-primary transition-colors"
               title="Volver al inicio">
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
                Materias que dicto
            </h1>
            <p class="text-on-surface-variant text-sm sm:text-base">
                Marca las materias que ofreces en tutorías.
                Solo aparecen las de semestres anteriores al tuyo
                (<c:out value="${tutor.carrera.nombre}"/> · semestre <c:out value="${tutor.semestre}"/>).
            </p>
        </div>

        <c:if test="${not empty mensaje}">
            <div class="mb-6 flex items-center gap-3 bg-green-50 text-green-700 text-sm font-medium px-4 py-3 rounded-lg border border-green-100">
                <span class="material-symbols-outlined text-base">check_circle</span>
                <c:out value="${mensaje}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="mb-6 flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <form id="formMaterias"
              method="post"
              action="${pageContext.request.contextPath}/tutor"
              class="space-y-6">
            <input type="hidden" name="ruta" value="guardar-materias"/>

            <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] p-4 sm:p-6 space-y-6">
                <c:choose>
                    <c:when test="${empty materiasPorSemestre}">
                        <p class="text-sm text-outline text-center py-8">
                            No hay materias disponibles para tu semestre y carrera.
                        </p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="entrada" items="${materiasPorSemestre}">
                            <div>
                                <h2 class="text-xs font-bold uppercase tracking-wider text-primary mb-2">
                                    Semestre <c:out value="${entrada.key}"/>
                                </h2>
                                <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                                    <c:forEach var="materia" items="${entrada.value}">
                                        <div class="relative">
                                            <input type="checkbox"
                                                   class="materia-check sr-only"
                                                   name="materias"
                                                   value="${materia.id}"
                                                   id="mat-${materia.id}"
                                                   <c:if test="${idsSeleccionados.contains(materia.id)}">checked</c:if>/>
                                            <label for="mat-${materia.id}"
                                                   class="block cursor-pointer rounded-lg border border-outline-variant
                                                          bg-surface-container-low px-3 py-2 text-left transition-all
                                                          hover:border-primary/40">
                                                <span class="block text-[10px] font-bold text-outline tracking-wide">
                                                    <c:out value="${materia.codigo}"/>
                                                </span>
                                                <span class="block text-xs font-semibold leading-snug mt-0.5">
                                                    <c:out value="${materia.nombre}"/>
                                                </span>
                                            </label>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="flex flex-col sm:flex-row sm:justify-end gap-3">
                <a href="${pageContext.request.contextPath}/tutor?ruta=inicio"
                   class="px-5 py-3 rounded-lg border border-outline-variant text-on-surface-variant
                          font-bold text-sm text-center hover:bg-surface-container-low transition-colors">
                    Cancelar
                </a>
                <button type="submit"
                        class="px-6 py-3 rounded-lg bg-gradient-to-br from-primary to-primary-container
                               text-on-primary font-bold text-sm shadow-md hover:opacity-90
                               active:scale-[0.98] transition-all inline-flex items-center justify-center gap-2"
                        style="font-family:'Manrope',sans-serif">
                    <span class="material-symbols-outlined text-base">save</span>
                    Guardar materias
                </button>
            </div>
        </form>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>

<script>
(function () {
    var form = document.getElementById('formMaterias');
    form.addEventListener('submit', function (e) {
        var checked = form.querySelectorAll('input[name="materias"]:checked');
        if (checked.length === 0) {
            e.preventDefault();
            alert('Selecciona al menos una materia.');
        }
    });
})();
</script>
</body>
</html>
