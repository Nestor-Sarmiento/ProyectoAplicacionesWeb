<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OwlShare - Inicio</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 min-h-screen flex items-center justify-center p-6">
    <div class="bg-white rounded-xl shadow-lg p-8 max-w-md w-full text-center space-y-4">
        <h1 class="text-2xl font-extrabold text-indigo-900" style="font-family:'Manrope',sans-serif">
            Sesión iniciada
        </h1>
        <p class="text-slate-600">
            Bienvenido/a, <strong><c:out value="${usuario.nombreCompleto}"/></strong>
        </p>
        <p class="text-sm text-slate-500">
            Tipo: <c:out value="${tipoUsuario}"/>
        </p>
        <p class="text-sm text-slate-400">
            <c:out value="${usuario.email}"/>
        </p>
    </div>
</body>
</html>
