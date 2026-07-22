<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Horarios del tutor</title>
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
        .slot-cell {
            min-height: 2.25rem;
            transition: background-color 0.15s ease, color 0.15s ease, box-shadow 0.15s ease;
        }
        .slot-cell.disponible {
            background-color: #24389c;
            color: #ffffff;
            box-shadow: inset 0 0 0 1px rgba(36, 56, 156, 0.4);
        }
        .slot-cell:not(.disponible):hover {
            background-color: #e8ecf8;
        }
        .calendario-scroll {
            scrollbar-width: thin;
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
    <div class="max-w-6xl mx-auto">
        <div class="mb-8 flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
            <div>
                <h1 class="text-3xl sm:text-4xl font-extrabold text-primary tracking-tight mb-2"
                    style="font-family:'Manrope',sans-serif">
                    Disponibilidad semanal
                </h1>
                <p class="text-on-surface-variant text-sm sm:text-base max-w-xl">
                    Toca las celdas del calendario para marcar las horas en las que puedes dar tutorías.
                    Azul = disponible.
                </p>
            </div>
            <div class="flex flex-wrap gap-2 text-xs text-on-surface-variant">
                <span class="inline-flex items-center gap-2 bg-surface-container-low px-3 py-2 rounded-lg">
                    <span class="w-3 h-3 rounded-sm bg-primary inline-block"></span>
                    Disponible
                </span>
                <span class="inline-flex items-center gap-2 bg-surface-container-low px-3 py-2 rounded-lg">
                    <span class="w-3 h-3 rounded-sm bg-surface-container-highest border border-outline-variant inline-block"></span>
                    No disponible
                </span>
            </div>
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

        <form id="formHorarios"
              method="post"
              action="${pageContext.request.contextPath}/tutor"
              class="space-y-6">
            <input type="hidden" name="ruta" value="guardar-horarios"/>
            <input type="hidden" name="slots" id="slotsInput" value=""/>

            <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] overflow-hidden">
                <div class="calendario-scroll overflow-x-auto">
                    <table class="w-full min-w-[720px] border-collapse" id="calendarioSemanal">
                        <thead>
                            <tr class="bg-surface-container-low">
                                <th class="sticky left-0 z-10 bg-surface-container-low px-3 py-3 text-left text-xs font-bold uppercase tracking-wider text-primary w-20">
                                    Hora
                                </th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Lun</th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Mar</th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Mié</th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Jue</th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Vie</th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Sáb</th>
                                <th class="px-2 py-3 text-center text-xs font-bold uppercase tracking-wider text-primary">Dom</th>
                            </tr>
                        </thead>
                        <tbody id="calendarioBody" class="divide-y divide-outline-variant/30">
                            <%-- Filas generadas por JS --%>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <p class="text-sm text-outline">
                    Seleccionadas: <strong id="contadorSlots" class="text-primary">0</strong> hora(s)
                </p>
                <div class="flex flex-wrap gap-3">
                    <button type="button" id="btnLimpiar"
                            class="px-5 py-3 rounded-lg border border-outline-variant text-on-surface-variant
                                   font-bold text-sm hover:bg-surface-container-low transition-colors">
                        Limpiar todo
                    </button>
                    <button type="submit"
                            class="px-6 py-3 rounded-lg bg-gradient-to-br from-primary to-primary-container
                                   text-on-primary font-bold text-sm shadow-md hover:opacity-90
                                   active:scale-[0.98] transition-all inline-flex items-center gap-2"
                            style="font-family:'Manrope',sans-serif">
                        <span class="material-symbols-outlined text-base">save</span>
                        Guardar disponibilidad
                    </button>
                </div>
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
    var DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];
    var HORA_INICIO = 7;
    var HORA_FIN = 21; // exclusive end for last slot start (20:00–21:00)

    var seleccion = new Set();
    var preseleccion = '<c:out value="${slotsSeleccionados}" default=""/>';
    if (preseleccion) {
        preseleccion.split(',').forEach(function (s) {
            if (s) seleccion.add(s.trim());
        });
    }

    var tbody = document.getElementById('calendarioBody');
    var slotsInput = document.getElementById('slotsInput');
    var contador = document.getElementById('contadorSlots');

    function formatearHora(h) {
        return (h < 10 ? '0' : '') + h + ':00';
    }

    function clave(dia, hora) {
        return dia + '-' + formatearHora(hora);
    }

    function actualizarContador() {
        contador.textContent = String(seleccion.size);
        slotsInput.value = Array.from(seleccion).sort().join(',');
    }

    function render() {
        tbody.innerHTML = '';
        for (var h = HORA_INICIO; h < HORA_FIN; h++) {
            var tr = document.createElement('tr');

            var th = document.createElement('td');
            th.className = 'sticky left-0 z-10 bg-surface-container-lowest px-3 py-1 text-xs font-semibold text-on-surface-variant whitespace-nowrap border-r border-outline-variant/30';
            th.textContent = formatearHora(h) + ' – ' + formatearHora(h + 1);
            tr.appendChild(th);

            DIAS.forEach(function (dia) {
                var td = document.createElement('td');
                td.className = 'p-1';

                var btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'slot-cell w-full rounded-md text-[10px] font-semibold bg-surface-container-highest';
                btn.dataset.dia = dia;
                btn.dataset.hora = String(h);
                btn.setAttribute('aria-label', dia + ' ' + formatearHora(h));
                btn.setAttribute('aria-pressed', 'false');

                var key = clave(dia, h);
                if (seleccion.has(key)) {
                    btn.classList.add('disponible');
                    btn.setAttribute('aria-pressed', 'true');
                }

                btn.addEventListener('click', function () {
                    var k = clave(this.dataset.dia, parseInt(this.dataset.hora, 10));
                    if (seleccion.has(k)) {
                        seleccion.delete(k);
                        this.classList.remove('disponible');
                        this.setAttribute('aria-pressed', 'false');
                    } else {
                        seleccion.add(k);
                        this.classList.add('disponible');
                        this.setAttribute('aria-pressed', 'true');
                    }
                    actualizarContador();
                });

                td.appendChild(btn);
                tr.appendChild(td);
            });

            tbody.appendChild(tr);
        }
        actualizarContador();
    }

    document.getElementById('btnLimpiar').addEventListener('click', function () {
        seleccion.clear();
        tbody.querySelectorAll('.slot-cell.disponible').forEach(function (el) {
            el.classList.remove('disponible');
            el.setAttribute('aria-pressed', 'false');
        });
        actualizarContador();
    });

    document.getElementById('formHorarios').addEventListener('submit', function () {
        actualizarContador();
    });

    render();
})();
</script>
</body>
</html>
