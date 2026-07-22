<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Buscar tutor</title>
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
                Hola, <strong class="text-on-surface"><c:out value="${estudiante.nombre}"/></strong>
            </span>
            <a href="${pageContext.request.contextPath}/estudiante?ruta=inicio"
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
    <div class="max-w-5xl mx-auto">
        <div class="mb-8">
            <h1 class="text-3xl sm:text-4xl font-extrabold text-primary tracking-tight mb-2"
                style="font-family:'Manrope',sans-serif">
                Buscar tutor
            </h1>
            <p class="text-on-surface-variant text-sm sm:text-base">
                Busca por materia de
                <strong class="text-on-surface"><c:out value="${estudiante.carrera.nombre}"/></strong>
                (hasta tu semestre <c:out value="${estudiante.semestre}"/>).
                Escribe para filtrar coincidencias.
            </p>
        </div>

        <c:if test="${not empty error}">
            <div class="mb-6 flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <form id="formBusqueda" method="get" action="${pageContext.request.contextPath}/estudiante"
              class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] p-6 sm:p-8 mb-10 space-y-4">
            <input type="hidden" name="ruta" value="buscar-tutor"/>
            <input type="hidden" name="asignaturaId" id="asignaturaId"
                   value="<c:out value='${asignaturaIdSeleccionada}'/>"/>

            <div class="space-y-1.5 relative">
                <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="materiaTexto">
                    Materia
                </label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline text-base pointer-events-none">
                        search
                    </span>
                    <input type="text" id="materiaTexto" autocomplete="off"
                           placeholder="Escribe código o nombre de la materia..."
                           value="<c:if test='${not empty materiaSeleccionada}'><c:out value='${materiaSeleccionada.codigo}'/> — <c:out value='${materiaSeleccionada.nombre}'/></c:if>"
                           class="w-full py-3 pl-10 pr-10 bg-surface-container-highest border-none rounded-lg
                                  focus:ring-2 focus:ring-indigo-300 outline-none"/>
                    <button type="button" id="btnLimpiarMateria"
                            class="absolute right-3 top-1/2 -translate-y-1/2 text-outline hover:text-primary hidden"
                            aria-label="Limpiar materia">
                        <span class="material-symbols-outlined text-sm">close</span>
                    </button>
                </div>

                <ul id="listaMaterias"
                    class="hidden absolute z-20 left-0 right-0 mt-1 max-h-64 overflow-y-auto
                           bg-surface-container-lowest rounded-lg border border-outline-variant/40
                           shadow-[0_12px_30px_rgba(25,28,30,0.12)]">
                </ul>
                <p id="hintMateria" class="text-xs text-outline">
                    Sin filtro se muestran tutores de todas tus materias hasta el semestre actual.
                    Haz clic en el campo para ver la lista completa.
                </p>
            </div>

            <button type="submit" id="btnBuscar"
                    class="inline-flex items-center gap-2 px-6 py-3 rounded-lg
                           bg-gradient-to-br from-primary to-primary-container text-on-primary
                           font-bold text-sm shadow-md hover:opacity-90 transition-all"
                    style="font-family:'Manrope',sans-serif">
                <span class="material-symbols-outlined text-base">search</span>
                Buscar tutores
            </button>
        </form>

        <c:if test="${busquedaRealizada}">
            <div class="flex items-center justify-between mb-6 gap-3 flex-wrap">
                <h2 class="text-2xl font-extrabold text-on-surface"
                    style="font-family:'Manrope',sans-serif">Tutores disponibles</h2>
                <span class="text-sm text-outline">
                    <c:out value="${tutores.size()}"/> resultado(s)
                    <c:if test="${not empty materiaSeleccionada}">
                        · <c:out value="${materiaSeleccionada.codigo}"/>
                    </c:if>
                    <c:if test="${empty materiaSeleccionada}">
                        · todas las materias
                    </c:if>
                </span>
            </div>

            <c:choose>
                <c:when test="${not empty tutores}">
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        <c:forEach var="tutor" items="${tutores}">
                            <article class="bg-surface-container-lowest rounded-xl p-6
                                            shadow-[0_20px_40px_rgba(25,28,30,0.08)]
                                            flex flex-col">
                                <div class="flex items-center gap-3 mb-4">
                                    <div class="w-12 h-12 rounded-full bg-primary text-on-primary
                                                flex items-center justify-center font-bold text-lg"
                                         style="font-family:'Manrope',sans-serif">
                                        <c:out value="${tutor.nombre.substring(0,1).toUpperCase()}"/>
                                    </div>
                                    <div>
                                        <h3 class="font-bold text-on-surface">
                                            <c:out value="${tutor.nombreCompleto}"/>
                                        </h3>
                                        <p class="text-xs text-on-surface-variant">
                                            <c:out value="${tutor.carrera.nombre}"/>
                                            · Semestre <c:out value="${tutor.semestre}"/>
                                        </p>
                                    </div>
                                </div>

                                <div class="mb-5 flex-grow">
                                    <p class="text-[10px] font-bold uppercase tracking-wider text-outline mb-2">Materias</p>
                                    <div class="flex flex-wrap gap-2">
                                        <c:forEach var="mat" items="${tutor.materias}" varStatus="st">
                                            <c:if test="${st.index < 3}">
                                                <span class="text-[11px] bg-indigo-50 text-primary px-2 py-1 rounded font-semibold">
                                                    <c:out value="${mat.codigo}"/>
                                                </span>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${tutor.materias.size() > 3}">
                                            <span class="text-[11px] text-outline">+${tutor.materias.size() - 3}</span>
                                        </c:if>
                                        <c:if test="${empty tutor.materias}">
                                            <span class="text-xs text-outline">Sin materias publicadas</span>
                                        </c:if>
                                    </div>
                                </div>

                                <a href="${pageContext.request.contextPath}/estudiante?ruta=detalle-tutor&id=${tutor.id}<c:if test='${not empty asignaturaIdSeleccionada}'>&asignaturaId=${asignaturaIdSeleccionada}</c:if>"
                                   class="w-full text-center bg-primary text-on-primary font-bold py-2.5 rounded-lg
                                          hover:opacity-90 text-sm transition-opacity">
                                    Ver perfil
                                </a>
                            </article>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] p-12 text-center">
                        <span class="material-symbols-outlined text-4xl text-outline/40 block mb-4">person_search</span>
                        <p class="text-on-surface-variant text-base">No hay tutores con esos criterios.</p>
                        <p class="text-outline text-sm mt-2">Prueba con otra materia o limpia el filtro.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>

<script>
(function () {
    var MATERIAS = ${empty materiasJson ? '[]' : materiasJson};

    var input = document.getElementById('materiaTexto');
    var hiddenId = document.getElementById('asignaturaId');
    var lista = document.getElementById('listaMaterias');
    var btnLimpiar = document.getElementById('btnLimpiarMateria');
    var form = document.getElementById('formBusqueda');
    var activo = -1;

    function actualizarBotones() {
        btnLimpiar.classList.toggle('hidden', !input.value);
    }

    function cerrarLista() {
        lista.classList.add('hidden');
        lista.innerHTML = '';
        activo = -1;
    }

    function seleccionar(materia) {
        hiddenId.value = String(materia.id);
        input.value = materia.etiqueta;
        cerrarLista();
        actualizarBotones();
        form.submit();
    }

    function renderLista(filtro) {
        var q = (filtro || '').trim().toLowerCase();
        var coincidencias = MATERIAS.filter(function (m) {
            if (!q) return true;
            return m.codigo.toLowerCase().indexOf(q) >= 0
                || m.nombre.toLowerCase().indexOf(q) >= 0
                || m.etiqueta.toLowerCase().indexOf(q) >= 0;
        });

        if (coincidencias.length === 0) {
            lista.innerHTML = '<li class="px-4 py-3 text-sm text-outline">Sin coincidencias</li>';
            lista.classList.remove('hidden');
            activo = -1;
            return;
        }

        lista.innerHTML = coincidencias.map(function (m, i) {
            return '<li role="option" data-index="' + i + '" data-id="' + m.id + '" '
                + 'class="px-4 py-3 cursor-pointer text-sm hover:bg-surface-container-low '
                + 'border-b border-outline-variant/20 last:border-0">'
                + '<span class="block text-[10px] font-bold text-outline uppercase">Semestre ' + m.semestre + '</span>'
                + '<span class="font-semibold text-on-surface">' + m.codigo + '</span>'
                + '<span class="text-on-surface-variant"> — ' + m.nombre + '</span>'
                + '</li>';
        }).join('');
        lista.classList.remove('hidden');
        activo = -1;

        lista.querySelectorAll('li[data-id]').forEach(function (li) {
            li.addEventListener('mousedown', function (e) {
                e.preventDefault();
                var id = Number(li.getAttribute('data-id'));
                var materia = MATERIAS.find(function (m) { return m.id === id; });
                if (materia) seleccionar(materia);
            });
        });
    }

    function resaltar(delta) {
        var items = lista.querySelectorAll('li[data-id]');
        if (!items.length) return;
        if (activo >= 0) items[activo].classList.remove('bg-surface-container-low');
        activo = (activo + delta + items.length) % items.length;
        items[activo].classList.add('bg-surface-container-low');
        items[activo].scrollIntoView({ block: 'nearest' });
    }

    input.addEventListener('focus', function () {
        renderLista(input.value);
    });

    input.addEventListener('input', function () {
        hiddenId.value = '';
        actualizarBotones();
        renderLista(input.value);
    });

    input.addEventListener('keydown', function (e) {
        if (lista.classList.contains('hidden')) {
            if (e.key === 'ArrowDown') {
                renderLista(input.value);
                e.preventDefault();
            }
            return;
        }
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            resaltar(1);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            resaltar(-1);
        } else if (e.key === 'Enter') {
            var items = lista.querySelectorAll('li[data-id]');
            if (activo >= 0 && items[activo]) {
                e.preventDefault();
                items[activo].dispatchEvent(new Event('mousedown'));
            }
        } else if (e.key === 'Escape') {
            cerrarLista();
        }
    });

    input.addEventListener('blur', function () {
        setTimeout(cerrarLista, 150);
    });

    btnLimpiar.addEventListener('click', function () {
        input.value = '';
        hiddenId.value = '';
        actualizarBotones();
        form.submit();
    });

    actualizarBotones();
})();
</script>
</body>
</html>
