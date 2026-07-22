<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>OwlShare - Iniciar sesión</title>
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

<%-- Top Nav --%>
<header class="bg-white/80 backdrop-blur-xl sticky top-0 z-50 shadow-[0_20px_40px_rgba(25,28,30,0.08)]">
    <div class="flex justify-between items-center w-full px-8 py-4 max-w-screen-2xl mx-auto">
        <span class="text-2xl font-extrabold text-indigo-900 tracking-tighter"
              style="font-family:'Manrope',sans-serif">OwlShare</span>
    </div>
</header>

<main class="flex-grow flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-md">
        <div class="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,30,0.08)] overflow-hidden">

            <%-- Header --%>
            <div class="bg-surface-container-low p-8 text-center">
                <h1 class="text-3xl font-extrabold text-primary tracking-tight mb-2"
                    style="font-family:'Manrope',sans-serif">Bienvenido</h1>
                <p class="text-on-surface-variant text-sm font-medium">
                    Intercambio académico entre estudiantes · OwlShare
                </p>
            </div>

            <div class="p-8 space-y-6">

                <%-- Mensaje informativo (p. ej. tras registro) --%>
                <c:if test="${not empty param.mensaje}">
                    <div class="flex items-center gap-3 bg-green-50 text-green-700 text-sm font-medium px-4 py-3 rounded-lg border border-green-100">
                        <span class="material-symbols-outlined text-base">check_circle</span>
                        <c:out value="${param.mensaje}"/>
                    </div>
                </c:if>

                <%-- Error del servidor --%>
                <c:if test="${not empty error}">
                    <div class="flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                        <span class="material-symbols-outlined text-base">error</span>
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <div id="errorValidacionLogin"
                     class="hidden flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg">
                    <span class="material-symbols-outlined text-base">error</span>
                    <span id="errorValidacionLoginMsg"></span>
                </div>

                <%-- POST /login?ruta=login --%>
                <form action="${pageContext.request.contextPath}/login"
                      method="post"
                      id="formLogin"
                      class="space-y-5"
                      novalidate>
                    <input type="hidden" name="ruta" value="login"/>

                    <div class="space-y-1.5">
                        <label class="text-xs font-semibold text-primary uppercase tracking-wider ml-1" for="email">
                            Email
                        </label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-outline text-sm">mail</span>
                            </div>
                            <input class="block w-full pl-10 pr-4 py-3 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 text-on-surface placeholder:text-outline/60
                                          outline-none transition-all"
                                   id="email" name="email" type="email"
                                   placeholder="estudiante@epn.edu.ec"
                                   maxlength="254"
                                   value="<c:out value='${param.email}'/>"
                                   required/>
                        </div>
                    </div>

                    <div class="space-y-1.5">
                        <label class="text-xs font-semibold text-primary uppercase tracking-wider" for="password">
                            Contraseña
                        </label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-outline text-sm">lock</span>
                            </div>
                            <input class="block w-full pl-10 pr-10 py-3 bg-surface-container-highest border-none rounded-lg
                                          focus:ring-2 focus:ring-indigo-300 text-on-surface placeholder:text-outline/60
                                          outline-none transition-all"
                                   id="password" name="password" type="password"
                                   placeholder="••••••••" required/>
                            <button type="button" id="togglePasswordLogin"
                                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-outline hover:text-primary transition-colors"
                                    aria-label="Mostrar u ocultar contraseña">
                                <span class="material-symbols-outlined text-sm">visibility</span>
                            </button>
                        </div>
                    </div>

                    <button type="submit"
                            class="w-full py-4 bg-gradient-to-br from-primary to-primary-container text-on-primary
                                   font-bold rounded-lg shadow-md hover:opacity-90 active:scale-[0.98] transition-all
                                   flex justify-center items-center gap-2"
                            style="font-family:'Manrope',sans-serif">
                        <span>Entrar</span>
                        <span class="material-symbols-outlined">arrow_forward</span>
                    </button>
                </form>

                <div class="text-center pt-2">
                    <p class="text-sm text-slate-500">
                        ¿No tienes una cuenta?
                        <a href="${pageContext.request.contextPath}/registro?ruta=mostrar"
                           class="text-primary font-bold hover:underline">Regístrate aquí</a>
                    </p>
                </div>
            </div>

            <div class="bg-surface-container h-1 w-full overflow-hidden">
                <div class="bg-secondary h-full w-1/3"></div>
            </div>
        </div>

        <div class="mt-8 grid grid-cols-2 gap-4">
            <div class="bg-surface-container-low p-4 rounded-lg flex items-center gap-3">
                <div class="bg-white p-2 rounded-full shadow-sm">
                    <span class="material-symbols-outlined text-secondary">verified</span>
                </div>
                <div>
                    <p class="text-[10px] font-bold text-on-surface-variant uppercase tracking-tighter">Material</p>
                    <p class="text-xs font-bold text-on-surface">Calidad curada</p>
                </div>
            </div>
            <div class="bg-surface-container-low p-4 rounded-lg flex items-center gap-3">
                <div class="bg-white p-2 rounded-full shadow-sm">
                    <span class="material-symbols-outlined text-primary">groups</span>
                </div>
                <div>
                    <p class="text-[10px] font-bold text-on-surface-variant uppercase tracking-tighter">Comunidad</p>
                    <p class="text-xs font-bold text-on-surface">Grupos de estudio</p>
                </div>
            </div>
        </div>
    </div>
</main>

<footer class="w-full py-8 mt-auto border-t border-slate-100 bg-slate-50">
    <div class="text-center">
        <span class="text-xs text-slate-400">© 2026 OwlShare. Todos los derechos reservados.</span>
    </div>
</footer>

<div class="fixed top-0 right-0 -z-10 w-1/3 h-full overflow-hidden pointer-events-none opacity-40">
    <div class="absolute top-[-10%] right-[-10%] w-[500px] h-[500px] bg-indigo-500/5 rounded-full blur-[100px]"></div>
</div>

<script src="${pageContext.request.contextPath}/js/validacion-campos.js"></script>
<script>
    (function () {
        var passwordInput = document.getElementById('password');
        var togglePasswordBtn = document.getElementById('togglePasswordLogin');
        var form = document.getElementById('formLogin');
        var errorBox = document.getElementById('errorValidacionLogin');
        var errorMsg = document.getElementById('errorValidacionLoginMsg');

        togglePasswordBtn.addEventListener('click', function (e) {
            e.preventDefault();
            var icon = this.querySelector('.material-symbols-outlined');
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.textContent = 'visibility_off';
            } else {
                passwordInput.type = 'password';
                icon.textContent = 'visibility';
            }
        });

        form.addEventListener('submit', function (e) {
            var emailError = OwlValidacion.validarEmail(document.getElementById('email').value);
            var mensaje = emailError;

            if (!passwordInput.value) {
                mensaje = mensaje || 'La contraseña es obligatoria.';
            }

            if (mensaje) {
                e.preventDefault();
                errorMsg.textContent = mensaje;
                errorBox.classList.remove('hidden');
                return;
            }
            errorBox.classList.add('hidden');
        });
    })();
</script>
</body>
</html>
