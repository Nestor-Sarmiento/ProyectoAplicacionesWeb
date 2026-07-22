<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Solicitar tutoría</title>
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
        #calendarioSemana {
            table-layout: fixed;
            width: 100%;
            border-collapse: collapse;
        }
        #calendarioSemana col.col-hora {
            width: 12%;
        }
        #calendarioSemana col.col-dia {
            width: calc(88% / 7);
        }
        #calendarioSemana th,
        #calendarioSemana td {
            overflow: hidden;
            vertical-align: middle;
            text-align: center;
            padding: 0.2rem;
        }
        #calendarioSemana th:first-child,
        #calendarioSemana td:first-child {
            background-color: #ffffff;
        }
        #calendarioSemana thead th:first-child {
            background-color: #f2f4f7;
        }
        .celda-hora {
            display: block;
            width: 100%;
            padding: 0.25rem 0.15rem;
            font-size: 0.65rem;
            font-weight: 600;
            color: #454652;
            text-align: center;
            line-height: 1.25;
            white-space: normal;
        }
        .slot-cell {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            height: 2.5rem;
            min-height: 2.5rem;
            max-height: 2.5rem;
            max-width: 100%;
            margin: 0 auto;
            padding: 0.15rem;
            box-sizing: border-box;
            overflow: hidden;
            text-align: center;
            line-height: 1.1;
            word-break: break-word;
            hyphens: auto;
            transition: background-color 0.15s ease, color 0.15s ease, box-shadow 0.15s ease;
        }
        .slot-cell.disponible {
            background-color: #e8ecf8;
            color: #24389c;
            cursor: pointer;
        }
        .slot-cell.disponible:hover {
            background-color: #d4dcf5;
        }
        .slot-cell.seleccionado {
            background-color: #24389c;
            color: #ffffff;
            box-shadow: inset 0 0 0 1px rgba(36, 56, 156, 0.4);
        }
        .slot-cell.ocupado {
            background-color: #f1f5f9;
            color: #94a3b8;
            cursor: not-allowed;
            font-size: 0.55rem;
            font-weight: 600;
        }
        .slot-cell.pasado,
        .slot-cell.no-disponible {
            background-color: #f8fafc;
            color: #cbd5e1;
            cursor: default;
            pointer-events: none;
        }
    </style>
</head>
<body class="bg-surface text-on-surface min-h-screen flex flex-col">

<header class="bg-white/80 backdrop-blur-xl sticky top-0 z-50 shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
    <div class="flex justify-between items-center w-full px-8 py-4 max-w-screen-2xl mx-auto">
        <span class="text-2xl font-extrabold text-indigo-900 tracking-tighter"
              style="font-family:'Manrope',sans-serif">OwlShare</span>
        <div class="flex items-center gap-4">
            <a href="${pageContext.request.contextPath}/estudiante?ruta=detalle-tutor&id=${tutor.id}<c:if test='${not empty asignaturaIdSeleccionada}'>&asignaturaId=${asignaturaIdSeleccionada}</c:if>"
               class="inline-flex items-center text-outline hover:text-primary transition-colors"
               title="Volver al perfil">
                <span class="material-symbols-outlined">arrow_back</span>
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
        <a href="${pageContext.request.contextPath}/estudiante?ruta=detalle-tutor&id=${tutor.id}<c:if test='${not empty asignaturaIdSeleccionada}'>&asignaturaId=${asignaturaIdSeleccionada}</c:if>"
           class="inline-flex items-center gap-1 text-sm font-bold text-primary hover:underline mb-4">
            <span class="material-symbols-outlined text-base">arrow_back</span>
            Volver al perfil del tutor
        </a>

        <div class="mb-8">
            <h1 class="text-3xl sm:text-4xl font-extrabold text-primary tracking-tight mb-2"
                style="font-family:'Manrope',sans-serif">
                Solicitar tutoría
            </h1>
            <p class="text-on-surface-variant text-sm sm:text-base">
                Con <strong class="text-on-surface"><c:out value="${tutor.nombreCompleto}"/></strong>.
                Puedes elegir un horario de la semana actual o la siguiente.
            </p>
        </div>

        <c:if test="${not empty error}">
            <div class="mb-6 flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                <span class="material-symbols-outlined text-base">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty materiasElegibles}">
                <div class="bg-surface-container-lowest rounded-xl p-8 shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
                    <p class="text-on-surface-variant">
                        Este tutor no imparte materias habilitadas para tu semestre.
                    </p>
                </div>
            </c:when>
            <c:otherwise>
                <form id="formSolicitud" method="post"
                      action="${pageContext.request.contextPath}/estudiante"
                      class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    <input type="hidden" name="ruta" value="enviar-solicitud"/>
                    <input type="hidden" name="tutorId" value="${tutor.id}"/>
                    <input type="hidden" name="disponibilidadId" id="disponibilidadId" value=""/>
                    <input type="hidden" name="fecha" id="fecha" value=""/>

                    <div class="lg:col-span-2 space-y-6">
                        <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] p-6 space-y-4">
                            <div class="space-y-1.5">
                                <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="asignaturaId">
                                    Materia <span class="text-red-600">*</span>
                                </label>
                                <c:choose>
                                    <c:when test="${materiaBloqueada}">
                                        <input type="hidden" name="asignaturaId" value="${materiaFija.id}"/>
                                        <div class="w-full py-3 px-4 bg-surface-container-highest rounded-lg text-sm font-semibold">
                                            <c:out value="${materiaFija.codigo}"/> — <c:out value="${materiaFija.nombre}"/>
                                        </div>
                                        <p class="text-xs text-outline">Fijada según tu búsqueda.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <select name="asignaturaId" id="asignaturaId" required
                                                class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                                       focus:ring-2 focus:ring-indigo-300 outline-none text-sm">
                                            <option value="">Selecciona una materia</option>
                                            <c:forEach var="materia" items="${materiasElegibles}">
                                                <option value="${materia.id}">
                                                    <c:out value="${materia.codigo}"/> — <c:out value="${materia.nombre}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="space-y-1.5">
                                <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="mensaje">
                                    Mensaje <span class="text-red-600">*</span>
                                </label>
                                <textarea name="mensaje" id="mensaje" rows="4" required maxlength="500" minlength="10"
                                          placeholder="Describe qué necesitas reforzar o en qué tema necesitas ayuda..."
                                          class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                                 focus:ring-2 focus:ring-indigo-300 outline-none text-sm resize-y"></textarea>
                                <p class="text-xs text-outline text-right"><span id="contadorMensaje">0</span> / 500</p>
                            </div>
                        </div>

                        <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] overflow-hidden">
                            <div class="px-6 py-4 border-b border-outline-variant/30 space-y-3">
                                <div class="flex flex-wrap items-center justify-between gap-3">
                                    <div>
                                        <h2 class="text-sm font-bold uppercase tracking-wider text-primary">
                                            Calendario
                                        </h2>
                                        <p class="text-xs text-outline mt-1">
                                            Solo puedes elegir horarios disponibles y libres.
                                        </p>
                                    </div>
                                    <div class="flex flex-wrap gap-2 text-xs text-on-surface-variant">
                                        <span class="inline-flex items-center gap-2 bg-surface-container-low px-3 py-1.5 rounded-lg">
                                            <span class="w-3 h-3 rounded-sm bg-[#e8ecf8] inline-block"></span> Disponible
                                        </span>
                                        <span class="inline-flex items-center gap-2 bg-surface-container-low px-3 py-1.5 rounded-lg">
                                            <span class="w-3 h-3 rounded-sm bg-primary inline-block"></span> Seleccionado
                                        </span>
                                        <span class="inline-flex items-center gap-2 bg-surface-container-low px-3 py-1.5 rounded-lg">
                                            <span class="w-3 h-3 rounded-sm bg-slate-100 inline-block"></span> Ocupado / pasado
                                        </span>
                                    </div>
                                </div>
                                <div class="flex items-center justify-between gap-3 bg-surface-container-low rounded-lg px-3 py-2">
                                    <button type="button" id="btnSemanaAnterior"
                                            class="inline-flex items-center gap-1 px-3 py-1.5 rounded-md text-sm font-bold
                                                   text-primary hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-transparent"
                                            disabled>
                                        <span class="material-symbols-outlined text-base">chevron_left</span>
                                        Anterior
                                    </button>
                                    <div class="text-center">
                                        <p id="etiquetaSemana" class="text-sm font-bold text-on-surface">Semana actual</p>
                                        <p id="rangoSemana" class="text-xs text-outline"></p>
                                    </div>
                                    <button type="button" id="btnSemanaSiguiente"
                                            class="inline-flex items-center gap-1 px-3 py-1.5 rounded-md text-sm font-bold
                                                   text-primary hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-transparent">
                                        Siguiente
                                        <span class="material-symbols-outlined text-base">chevron_right</span>
                                    </button>
                                </div>
                            </div>
                            <div class="p-2 overflow-hidden">
                                <table id="calendarioSemana">
                                    <colgroup>
                                        <col class="col-hora"/>
                                        <col class="col-dia"/><col class="col-dia"/><col class="col-dia"/>
                                        <col class="col-dia"/><col class="col-dia"/><col class="col-dia"/>
                                        <col class="col-dia"/>
                                    </colgroup>
                                    <thead>
                                        <tr class="bg-surface-container-low" id="calendarioHead">
                                            <th class="py-3 text-xs font-bold uppercase tracking-wider text-primary">
                                                Hora
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody id="calendarioBody" class="divide-y divide-outline-variant/30"></tbody>
                                </table>
                            </div>
                            <p id="errorHorario" class="hidden px-6 pb-4 text-xs text-red-600">
                                Selecciona un horario disponible en el calendario.
                            </p>
                        </div>
                    </div>

                    <aside class="lg:col-span-1">
                        <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] p-6 sticky top-24 space-y-4">
                            <h3 class="text-lg font-extrabold text-primary" style="font-family:'Manrope',sans-serif">
                                Resumen
                            </h3>
                            <div class="flex items-center gap-3">
                                <div class="w-12 h-12 rounded-full bg-primary text-on-primary flex items-center justify-center font-bold">
                                    <c:out value="${tutor.nombre.substring(0,1).toUpperCase()}"/>
                                </div>
                                <div>
                                    <p class="font-bold text-sm"><c:out value="${tutor.nombreCompleto}"/></p>
                                    <p class="text-xs text-outline"><c:out value="${tutor.carrera.nombre}"/></p>
                                </div>
                            </div>
                            <div class="text-sm space-y-2 border-t border-outline-variant/30 pt-4">
                                <p>
                                    <span class="text-[10px] font-bold uppercase text-outline tracking-wider">Horario elegido</span><br/>
                                    <span id="resumenHorario" class="font-semibold text-on-surface">Ninguno</span>
                                </p>
                            </div>
                            <p class="text-xs text-outline border-t border-outline-variant/30 pt-4">
                                Tu solicitud quedará en estado <strong>pendiente</strong> hasta que el tutor acepte o rechace.
                            </p>
                            <button type="submit" id="btnEnviar"
                                    class="w-full inline-flex items-center justify-center gap-2 px-6 py-3 rounded-lg
                                           bg-gradient-to-br from-primary to-primary-container text-on-primary
                                           font-bold text-sm shadow-md hover:opacity-90 transition-all"
                                    style="font-family:'Manrope',sans-serif">
                                <span class="material-symbols-outlined text-base">send</span>
                                Enviar solicitud
                            </button>
                        </div>
                    </aside>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>

<script>
(function () {
    var SEMANAS = ${empty semanasJson ? '[]' : semanasJson};
    var DISP_IDS = ${empty disponibilidadJson ? '{}' : disponibilidadJson};
    var disponibles = {};
    '<c:out value="${slotsDisponibles}" default=""/>'.split(',').forEach(function (s) {
        if (s) disponibles[s.trim()] = true;
    });
    var ocupados = {};
    '<c:out value="${slotsOcupados}" default=""/>'.split(',').forEach(function (s) {
        if (s) ocupados[s.trim()] = true;
    });
    var hoyIso = '<c:out value="${hoyIso}"/>';
    var horaActual = '<c:out value="${horaActual}"/>';

    var HORA_INICIO = 7;
    var HORA_FIN = 21;
    var semanaIdx = 0;
    var seleccion = null;
    var theadRow = document.getElementById('calendarioHead');
    var tbody = document.getElementById('calendarioBody');
    var inputDisp = document.getElementById('disponibilidadId');
    var inputFecha = document.getElementById('fecha');
    var resumen = document.getElementById('resumenHorario');
    var errorHorario = document.getElementById('errorHorario');
    var mensaje = document.getElementById('mensaje');
    var contador = document.getElementById('contadorMensaje');
    var etiquetaSemana = document.getElementById('etiquetaSemana');
    var rangoSemana = document.getElementById('rangoSemana');
    var btnAnterior = document.getElementById('btnSemanaAnterior');
    var btnSiguiente = document.getElementById('btnSemanaSiguiente');

    function formatearHora(h) {
        return (h < 10 ? '0' : '') + h + ':00';
    }

    function claveSemanal(dia, hora) {
        return dia + '-' + formatearHora(hora);
    }

    function claveOcupado(dispId, fecha) {
        return dispId + '|' + fecha;
    }

    function esPasado(fecha, horaStr) {
        if (fecha < hoyIso) return true;
        if (fecha === hoyIso && horaStr <= horaActual) return true;
        return false;
    }

    function columnasActuales() {
        return (SEMANAS[semanaIdx] && SEMANAS[semanaIdx].columnas) || [];
    }

    function limpiarSeleccion() {
        seleccion = null;
        if (inputDisp) inputDisp.value = '';
        if (inputFecha) inputFecha.value = '';
        if (resumen) resumen.textContent = 'Ninguno';
    }

    function actualizarNav() {
        if (etiquetaSemana && SEMANAS[semanaIdx]) {
            etiquetaSemana.textContent = SEMANAS[semanaIdx].etiqueta;
        }
        if (rangoSemana && SEMANAS[semanaIdx]) {
            rangoSemana.textContent = SEMANAS[semanaIdx].inicio + ' – ' + SEMANAS[semanaIdx].fin;
        }
        if (btnAnterior) btnAnterior.disabled = semanaIdx <= 0;
        if (btnSiguiente) btnSiguiente.disabled = semanaIdx >= SEMANAS.length - 1;
    }

    function renderCabecera() {
        if (!theadRow) return;
        while (theadRow.children.length > 1) {
            theadRow.removeChild(theadRow.lastChild);
        }
        columnasActuales().forEach(function (col) {
            var th = document.createElement('th');
            th.className = 'py-3 text-xs font-bold uppercase tracking-wider text-primary text-center';
            th.innerHTML = '<span class="block">' + col.etiqueta + '</span>'
                + '<span class="block font-semibold text-outline normal-case tracking-normal mt-0.5">'
                + col.fechaCorta + '</span>';
            theadRow.appendChild(th);
        });
    }

    function render() {
        if (!tbody) return;
        renderCabecera();
        actualizarNav();
        tbody.innerHTML = '';
        var columnas = columnasActuales();

        for (var h = HORA_INICIO; h < HORA_FIN; h++) {
            var tr = document.createElement('tr');
            var th = document.createElement('td');
            th.innerHTML = '<span class="celda-hora">' + formatearHora(h)
                + '<br/>' + formatearHora(h + 1) + '</span>';
            tr.appendChild(th);

            columnas.forEach(function (col) {
                var td = document.createElement('td');
                td.className = 'p-1';
                var btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'slot-cell rounded-md text-[10px] font-semibold';

                var keySemanal = claveSemanal(col.dia, h);
                var dispId = DISP_IDS[keySemanal];
                var horaStr = formatearHora(h);

                if (!disponibles[keySemanal] || !dispId) {
                    btn.classList.add('no-disponible');
                    btn.disabled = true;
                } else if (ocupados[claveOcupado(dispId, col.fecha)] || esPasado(col.fecha, horaStr)) {
                    btn.classList.add('ocupado');
                    btn.disabled = true;
                    btn.textContent = 'No disponible';
                } else {
                    btn.classList.add('disponible');
                    btn.dataset.dispId = String(dispId);
                    btn.dataset.fecha = col.fecha;
                    btn.dataset.label = col.etiqueta + ' ' + col.fechaCorta + ' · ' + horaStr;
                    if (seleccion && seleccion.dispId === String(dispId) && seleccion.fecha === col.fecha) {
                        btn.classList.remove('disponible');
                        btn.classList.add('seleccionado');
                    }
                    btn.addEventListener('click', function () {
                        seleccionar(this);
                    });
                }
                td.appendChild(btn);
                tr.appendChild(td);
            });
            tbody.appendChild(tr);
        }
    }

    function seleccionar(btn) {
        tbody.querySelectorAll('.slot-cell.seleccionado').forEach(function (el) {
            el.classList.remove('seleccionado');
            el.classList.add('disponible');
        });
        btn.classList.remove('disponible');
        btn.classList.add('seleccionado');
        seleccion = {
            dispId: btn.dataset.dispId,
            fecha: btn.dataset.fecha,
            label: btn.dataset.label
        };
        inputDisp.value = seleccion.dispId;
        inputFecha.value = seleccion.fecha;
        resumen.textContent = seleccion.label;
        errorHorario.classList.add('hidden');
    }

    function cambiarSemana(delta) {
        var nuevo = semanaIdx + delta;
        if (nuevo < 0 || nuevo >= SEMANAS.length) return;
        semanaIdx = nuevo;
        limpiarSeleccion();
        render();
    }

    if (btnAnterior) {
        btnAnterior.addEventListener('click', function () { cambiarSemana(-1); });
    }
    if (btnSiguiente) {
        btnSiguiente.addEventListener('click', function () { cambiarSemana(1); });
    }

    if (mensaje && contador) {
        mensaje.addEventListener('input', function () {
            contador.textContent = String(mensaje.value.length);
        });
    }

    var form = document.getElementById('formSolicitud');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (!inputDisp.value || !inputFecha.value) {
                e.preventDefault();
                errorHorario.classList.remove('hidden');
                return;
            }
            if (!mensaje.value || mensaje.value.trim().length < 10) {
                e.preventDefault();
                mensaje.focus();
            }
        });
    }

    render();
})();
</script>
</body>
</html>
