<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Registro</title>
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
        <a href="${pageContext.request.contextPath}/login?ruta=ingresar"
           class="text-2xl font-extrabold text-indigo-900 tracking-tighter"
           style="font-family:'Manrope',sans-serif">OwlShare</a>
        <a href="${pageContext.request.contextPath}/login?ruta=ingresar"
           class="text-sm font-semibold text-primary hover:underline">Iniciar sesión</a>
    </div>
</header>

<main class="flex-grow px-4 sm:px-6 py-10">
    <div class="max-w-3xl mx-auto">
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] overflow-hidden">
            <div class="bg-surface-container-low p-8 text-center">
                <h1 class="text-3xl font-extrabold text-primary tracking-tight mb-2"
                    style="font-family:'Manrope',sans-serif">Crear cuenta</h1>
                <p class="text-on-surface-variant text-sm font-medium">
                    Únete a OwlShare como estudiante o tutor
                </p>
            </div>

            <div class="p-6 sm:p-8 space-y-6">
                <c:if test="${not empty error}">
                    <div class="flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                        <span class="material-symbols-outlined text-base">error</span>
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <div id="errorValidacion"
                     class="hidden flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                    <span class="material-symbols-outlined text-base">error</span>
                    <span id="errorValidacionMsg"></span>
                </div>

                <p class="text-xs text-on-surface-variant">
                    Los campos marcados con <span class="text-red-600 font-bold">*</span> son obligatorios.
                </p>

                <form id="formRegistro"
                      method="post"
                      action="${pageContext.request.contextPath}/registro"
                      class="space-y-6"
                      novalidate>
                    <input type="hidden" name="ruta" value="registrar"/>

                    <%-- Tipo de cuenta --%>
                    <div class="space-y-3">
                        <p class="text-xs font-semibold text-primary uppercase tracking-wider">
                            Tipo de cuenta <span class="text-red-600">*</span>
                        </p>
                        <div class="grid grid-cols-2 gap-3">
                            <label class="cursor-pointer">
                                <input type="radio" name="tipo" value="ESTUDIANTE" class="peer sr-only" checked>
                                <span class="flex items-center justify-center gap-2 py-3 px-4 rounded-lg border border-outline-variant
                                             peer-checked:bg-primary peer-checked:text-on-primary peer-checked:border-primary
                                             font-bold text-sm transition-all">
                                    <span class="material-symbols-outlined text-base">school</span>
                                    Estudiante
                                </span>
                            </label>
                            <label class="cursor-pointer">
                                <input type="radio" name="tipo" value="TUTOR" class="peer sr-only">
                                <span class="flex items-center justify-center gap-2 py-3 px-4 rounded-lg border border-outline-variant
                                             peer-checked:bg-primary peer-checked:text-on-primary peer-checked:border-primary
                                             font-bold text-sm transition-all">
                                    <span class="material-symbols-outlined text-base">person</span>
                                    Tutor
                                </span>
                            </label>
                        </div>
                    </div>

                    <%-- Cuenta --%>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="sm:col-span-2 space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="email">
                                Correo <span class="text-red-600">*</span>
                            </label>
                            <input id="email" name="email" type="email" required maxlength="254"
                                   placeholder="estudiante@epn.edu.ec"
                                   class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 outline-none"/>
                        </div>
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="password">
                                Contraseña <span class="text-red-600">*</span>
                            </label>
                            <div class="relative">
                                <input id="password" name="password" type="password" required minlength="8" maxlength="72"
                                       placeholder="Mínimo 8 caracteres"
                                       class="w-full py-3 pl-4 pr-11 bg-surface-container-highest border-none rounded-lg
                                              focus:ring-2 focus:ring-indigo-300 outline-none"/>
                                <button type="button" id="togglePassword"
                                        class="absolute right-3 top-1/2 -translate-y-1/2 text-outline hover:text-primary transition-colors"
                                        aria-label="Mostrar u ocultar contraseña">
                                    <span class="material-symbols-outlined text-sm">visibility</span>
                                </button>
                            </div>
                        </div>
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="passwordConfirm">
                                Confirmar contraseña <span class="text-red-600">*</span>
                            </label>
                            <div class="relative">
                                <input id="passwordConfirm" name="passwordConfirm" type="password" required minlength="8" maxlength="72"
                                       class="w-full py-3 pl-4 pr-11 bg-surface-container-highest border-none rounded-lg
                                              focus:ring-2 focus:ring-indigo-300 outline-none"/>
                                <button type="button" id="togglePasswordConfirm"
                                        class="absolute right-3 top-1/2 -translate-y-1/2 text-outline hover:text-primary transition-colors"
                                        aria-label="Mostrar u ocultar confirmación">
                                    <span class="material-symbols-outlined text-sm">visibility</span>
                                </button>
                            </div>
                            <p id="msgPasswordMatch" class="hidden text-xs font-medium mt-1"></p>
                        </div>
                    </div>

                    <%-- Nombres --%>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="primerNombre">
                                Primer nombre <span class="text-red-600">*</span>
                            </label>
                            <input id="primerNombre" name="primerNombre" type="text" required maxlength="50"
                                   class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 outline-none"/>
                        </div>
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="segundoNombre">
                                Segundo nombre <span class="normal-case text-outline font-medium">(opcional)</span>
                            </label>
                            <input id="segundoNombre" name="segundoNombre" type="text" maxlength="50"
                                   class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 outline-none"/>
                        </div>
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="primerApellido">
                                Primer apellido <span class="text-red-600">*</span>
                            </label>
                            <input id="primerApellido" name="primerApellido" type="text" required maxlength="50"
                                   class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 outline-none"/>
                        </div>
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="segundoApellido">
                                Segundo apellido <span class="normal-case text-outline font-medium">(opcional)</span>
                            </label>
                            <input id="segundoApellido" name="segundoApellido" type="text" maxlength="50"
                                   class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 outline-none"/>
                        </div>
                    </div>

                    <%-- Académico --%>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="carreraId">
                                Carrera <span class="text-red-600">*</span>
                            </label>
                            <select id="carreraId" name="carreraId" required
                                    class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                           focus:ring-2 focus:ring-indigo-300 outline-none">
                                <option value="">Selecciona una carrera</option>
                                <c:forEach var="carrera" items="${carreras}">
                                    <option value="${carrera.id}"><c:out value="${carrera.nombre}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="space-y-1.5">
                            <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="semestre">
                                Semestre <span class="text-red-600">*</span>
                            </label>
                            <select id="semestre" name="semestre" required
                                    class="w-full py-3 px-4 bg-surface-container-highest border-none rounded-lg
                                           focus:ring-2 focus:ring-indigo-300 outline-none">
                            </select>
                        </div>
                    </div>

                    <%-- Materias tutor --%>
                    <section id="seccionMaterias" class="hidden space-y-3">
                        <div>
                            <h2 class="text-sm font-bold text-primary uppercase tracking-wider">
                                Materias a dictar <span class="text-red-600">*</span>
                            </h2>
                            <p class="text-xs text-on-surface-variant mt-1">
                                Solo puedes elegir materias ya cursadas (semestres anteriores al tuyo).
                                Marca al menos una materia.
                            </p>
                        </div>
                        <div id="materiasContainer"
                             class="bg-surface-container-low rounded-xl p-4 max-h-96 overflow-y-auto space-y-5">
                            <p class="text-sm text-outline text-center py-6" id="materiasPlaceholder">
                                Selecciona carrera y semestre para ver las materias disponibles.
                            </p>
                        </div>
                    </section>

                    <button type="submit"
                            class="w-full py-4 bg-gradient-to-br from-primary to-primary-container text-on-primary
                                   font-bold rounded-lg shadow-md hover:opacity-90 active:scale-[0.98] transition-all
                                   flex justify-center items-center gap-2"
                            style="font-family:'Manrope',sans-serif">
                        <span>Registrarme</span>
                        <span class="material-symbols-outlined">arrow_forward</span>
                    </button>
                </form>

                <p class="text-center text-sm text-slate-500">
                    ¿Ya tienes cuenta?
                    <a href="${pageContext.request.contextPath}/login?ruta=ingresar"
                       class="text-primary font-bold hover:underline">Inicia sesión</a>
                </p>
            </div>
        </div>
    </div>
</main>

<footer class="w-full py-6 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare · Intercambio académico entre estudiantes</span>
    </div>
</footer>

<script>
(function () {
    var CATALOGO = ${empty catalogoJson ? '{}' : catalogoJson};
    var ETIQUETAS = {
        1: '1ro', 2: '2do', 3: '3ro', 4: '4to', 5: '5to',
        6: '6to', 7: '7mo', 8: '8vo', 9: '9no'
    };

    var tipoRadios = document.querySelectorAll('input[name="tipo"]');
    var semestreSelect = document.getElementById('semestre');
    var carreraSelect = document.getElementById('carreraId');
    var seccionMaterias = document.getElementById('seccionMaterias');
    var materiasContainer = document.getElementById('materiasContainer');
    var form = document.getElementById('formRegistro');
    var errorBox = document.getElementById('errorValidacion');
    var errorMsg = document.getElementById('errorValidacionMsg');
    var passwordInput = document.getElementById('password');
    var passwordConfirmInput = document.getElementById('passwordConfirm');
    var msgPasswordMatch = document.getElementById('msgPasswordMatch');

    function configurarToggle(btnId, input) {
        var btn = document.getElementById(btnId);
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            var icon = this.querySelector('.material-symbols-outlined');
            if (input.type === 'password') {
                input.type = 'text';
                icon.textContent = 'visibility_off';
            } else {
                input.type = 'password';
                icon.textContent = 'visibility';
            }
        });
    }

    function verificarPasswordMatch() {
        var password = passwordInput.value;
        var confirm = passwordConfirmInput.value;

        if (!confirm) {
            msgPasswordMatch.classList.add('hidden');
            passwordConfirmInput.classList.remove('ring-2', 'ring-red-300', 'ring-green-300');
            return password.length === 0 || password.length >= 8;
        }

        msgPasswordMatch.classList.remove('hidden');
        if (password !== confirm) {
            msgPasswordMatch.textContent = 'Las contraseñas no coinciden.';
            msgPasswordMatch.className = 'text-xs font-medium mt-1 text-red-600';
            passwordConfirmInput.classList.remove('ring-green-300');
            passwordConfirmInput.classList.add('ring-2', 'ring-red-300');
            return false;
        }

        msgPasswordMatch.textContent = 'Las contraseñas coinciden.';
        msgPasswordMatch.className = 'text-xs font-medium mt-1 text-green-600';
        passwordConfirmInput.classList.remove('ring-red-300');
        passwordConfirmInput.classList.add('ring-2', 'ring-green-300');
        return true;
    }

    configurarToggle('togglePassword', passwordInput);
    configurarToggle('togglePasswordConfirm', passwordConfirmInput);
    passwordInput.addEventListener('input', verificarPasswordMatch);
    passwordConfirmInput.addEventListener('input', verificarPasswordMatch);

    function tipoActual() {
        var checked = document.querySelector('input[name="tipo"]:checked');
        return checked ? checked.value : 'ESTUDIANTE';
    }

    function esTutor() {
        return tipoActual() === 'TUTOR';
    }

    function llenarSemestres() {
        var min = esTutor() ? 2 : 1;
        var max = esTutor() ? 9 : 8;
        var valorPrev = semestreSelect.value;
        semestreSelect.innerHTML = '<option value="">Selecciona un semestre</option>';
        for (var s = min; s <= max; s++) {
            var opt = document.createElement('option');
            opt.value = String(s);
            opt.textContent = ETIQUETAS[s] + ' semestre';
            semestreSelect.appendChild(opt);
        }
        if (valorPrev && Number(valorPrev) >= min && Number(valorPrev) <= max) {
            semestreSelect.value = valorPrev;
        }
    }

    function renderMaterias() {
        if (!esTutor()) {
            seccionMaterias.classList.add('hidden');
            materiasContainer.innerHTML = '';
            return;
        }

        seccionMaterias.classList.remove('hidden');
        var carreraId = carreraSelect.value;
        var semestre = parseInt(semestreSelect.value, 10);

        if (!carreraId || !semestre) {
            materiasContainer.innerHTML =
                '<p class="text-sm text-outline text-center py-6">Selecciona carrera y semestre para ver las materias disponibles.</p>';
            return;
        }

        var lista = CATALOGO[carreraId] || [];
        var porSemestre = {};
        lista.forEach(function (a) {
            if (a.semestre < semestre) {
                if (!porSemestre[a.semestre]) {
                    porSemestre[a.semestre] = [];
                }
                porSemestre[a.semestre].push(a);
            }
        });

        var semestres = Object.keys(porSemestre).map(Number).sort(function (a, b) { return a - b; });
        if (semestres.length === 0) {
            materiasContainer.innerHTML =
                '<p class="text-sm text-outline text-center py-6">No hay materias previas para este semestre.</p>';
            return;
        }

        var html = '';
        semestres.forEach(function (sem) {
            html += '<div>';
            html += '<h3 class="text-xs font-bold uppercase tracking-wider text-primary mb-2">'
                + ETIQUETAS[sem] + ' semestre</h3>';
            html += '<div class="grid grid-cols-1 sm:grid-cols-2 gap-2">';
            porSemestre[sem].forEach(function (a) {
                html += '<div class="relative">';
                html += '<input type="checkbox" class="materia-check peer sr-only" name="materias" value="'
                    + a.id + '" id="mat-' + a.id + '"/>';
                html += '<label for="mat-' + a.id + '" class="block cursor-pointer rounded-lg border border-outline-variant '
                    + 'bg-surface-container-lowest px-3 py-2 text-left transition-all hover:border-primary/40">';
                html += '<span class="block text-[10px] font-bold text-outline tracking-wide">' + a.codigo + '</span>';
                html += '<span class="block text-xs font-semibold leading-snug mt-0.5">' + a.nombre + '</span>';
                html += '</label></div>';
            });
            html += '</div></div>';
        });
        materiasContainer.innerHTML = html;
    }

    tipoRadios.forEach(function (radio) {
        radio.addEventListener('change', function () {
            llenarSemestres();
            renderMaterias();
        });
    });
    carreraSelect.addEventListener('change', renderMaterias);
    semestreSelect.addEventListener('change', renderMaterias);

    form.addEventListener('submit', function (e) {
        var mensaje = null;
        var email = document.getElementById('email').value.trim();
        var password = passwordInput.value;
        var confirm = passwordConfirmInput.value;
        var coinciden = verificarPasswordMatch();

        if (!email) mensaje = 'El correo es obligatorio.';
        else if (!password) mensaje = 'La contraseña es obligatoria.';
        else if (password.length < 8) mensaje = 'La contraseña debe tener al menos 8 caracteres.';
        else if (!confirm) mensaje = 'Debes confirmar la contraseña.';
        else if (!coinciden || password !== confirm) mensaje = 'Las contraseñas no coinciden.';
        else if (!document.getElementById('primerNombre').value.trim()) mensaje = 'El primer nombre es obligatorio.';
        else if (!document.getElementById('primerApellido').value.trim()) mensaje = 'El primer apellido es obligatorio.';
        else if (!carreraSelect.value) mensaje = 'Selecciona una carrera.';
        else if (!semestreSelect.value) mensaje = 'Selecciona un semestre.';
        else if (esTutor()) {
            var checked = materiasContainer.querySelectorAll('input[name="materias"]:checked');
            if (checked.length === 0) {
                mensaje = 'Selecciona al menos una materia para dictar.';
            }
        }

        if (mensaje) {
            e.preventDefault();
            errorMsg.textContent = mensaje;
            errorBox.classList.remove('hidden');
            errorBox.scrollIntoView({ behavior: 'smooth', block: 'center' });
            return;
        }
        errorBox.classList.add('hidden');
    });

    llenarSemestres();
    renderMaterias();
})();
</script>
</body>
</html>
