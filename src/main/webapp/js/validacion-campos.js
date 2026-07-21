/**
 * Validaciones y restricciones de entrada en formularios OwlShare.
 */
(function (global) {
    'use strict';

    var LIMITES = {
        emailMax: 254,
        nombreMax: 50,
        passwordMin: 8,
        passwordMax: 72,
        comentarioMentoriaMax: 300
    };

    var PATRONES = {
        email: /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
        nombre: /^[\p{L}][\p{L}\s'-]{0,49}$/u,
        comentarioMentoria: /^[\p{L}\p{N}\s.,;:!?¡¿()\-'"'\n]{1,300}$/u
    };

    function soloNombre(valor) {
        return valor.replace(/[^\p{L}\s'-]/gu, '');
    }

    function aplicarLimiteNombre(input) {
        if (!input) {
            return;
        }
        input.setAttribute('maxlength', String(LIMITES.nombreMax));
        input.addEventListener('input', function () {
            var filtrado = soloNombre(input.value);
            if (filtrado.length > LIMITES.nombreMax) {
                filtrado = filtrado.slice(0, LIMITES.nombreMax);
            }
            if (input.value !== filtrado) {
                input.value = filtrado;
            }
        });
    }

    function validarEmail(valor) {
        if (!valor || !valor.trim()) {
            return 'El correo electrónico es obligatorio.';
        }
        if (valor.trim().length > LIMITES.emailMax) {
            return 'El correo no puede superar ' + LIMITES.emailMax + ' caracteres.';
        }
        if (!PATRONES.email.test(valor.trim())) {
            return 'Ingresa un correo válido.';
        }
        return null;
    }

    function validarPassword(valor) {
        if (!valor) {
            return 'La contraseña es obligatoria.';
        }
        if (valor.length < LIMITES.passwordMin) {
            return 'La contraseña debe tener al menos ' + LIMITES.passwordMin + ' caracteres.';
        }
        if (valor.length > LIMITES.passwordMax) {
            return 'La contraseña no puede superar ' + LIMITES.passwordMax + ' caracteres.';
        }
        if (/\s/.test(valor)) {
            return 'La contraseña no puede contener espacios.';
        }
        return null;
    }

    function validarNombre(valor, etiqueta, obligatorio) {
        if (!valor || !valor.trim()) {
            return obligatorio ? ('El ' + etiqueta + ' es obligatorio.') : null;
        }
        if (!PATRONES.nombre.test(valor.trim())) {
            return 'El ' + etiqueta + ' solo puede contener letras, espacios, guiones o apóstrofes.';
        }
        return null;
    }

    function validarComentarioMentoria(valor) {
        if (!valor || !valor.trim()) {
            return 'El comentario es obligatorio.';
        }
        if (valor.trim().length > LIMITES.comentarioMentoriaMax) {
            return 'El comentario no puede superar ' + LIMITES.comentarioMentoriaMax + ' caracteres.';
        }
        if (!PATRONES.comentarioMentoria.test(valor.trim())) {
            return 'El comentario contiene caracteres no permitidos.';
        }
        return null;
    }

    global.OwlValidacion = {
        LIMITES: LIMITES,
        PATRONES: PATRONES,
        soloNombre: soloNombre,
        aplicarLimiteNombre: aplicarLimiteNombre,
        validarEmail: validarEmail,
        validarPassword: validarPassword,
        validarNombre: validarNombre,
        validarComentarioMentoria: validarComentarioMentoria
    };
})(typeof window !== 'undefined' ? window : this);
