# CU-08 — Solicitar una tutoría

## Actores

- **Estudiante** (actor principal): usuario autenticado que necesita apoyo en una materia y solicita una sesión con un tutor.
- **Tutor** (actor secundario, pasivo): recibe la solicitud en estado pendiente; no participa activamente en este caso de uso, pero debe tener materias y horarios configurados para que la solicitud sea posible.

## Descripción

El estudiante elige un tutor, selecciona la materia, un horario disponible en el calendario (semana actual o siguiente) y redacta un mensaje explicando qué necesita. Al enviar la solicitud, el sistema la registra en estado **Pendiente** hasta que el tutor la acepte o rechace.

Este caso de uso puede iniciarse desde el perfil del tutor o desde la búsqueda de tutores, con la materia preseleccionada si el estudiante llegó filtrando por una asignatura.

## Precondiciones

1. El estudiante tiene una sesión activa en OwlShare.
2. Existe al menos un tutor activo con la materia deseada en su oferta y con horarios de disponibilidad configurados.
3. La materia solicitada corresponde al semestre del estudiante (no puede pedir tutoría de materias de semestres superiores al suyo).
4. El horario elegido pertenece a la **semana actual** o a la **semana siguiente**, no ha pasado y no está ocupado por otra solicitud pendiente o aceptada del mismo tutor.

## Postcondiciones

### Éxito

- Se crea una solicitud de tutoría en estado **Pendiente**.
- La solicitud queda visible en **Mis solicitudes** del estudiante.
- El horario seleccionado queda **bloqueado** para otros estudiantes mientras la solicitud esté pendiente o aceptada.
- El estudiante recibe confirmación visual de que la solicitud fue enviada.

### Fallo

- No se crea ninguna solicitud.
- El estudiante permanece en el formulario o vuelve a él con un mensaje que explica el motivo.

## Flujo principal

1. El estudiante accede al perfil de un tutor o inicia el flujo **Solicitar tutoría** desde la búsqueda.
2. El sistema muestra las materias que el tutor dicta y que el estudiante puede solicitar según su semestre.
3. El estudiante selecciona la materia (si venía desde una búsqueda por materia, esta puede aparecer ya seleccionada).
4. El sistema muestra un calendario con la **semana actual** y la **semana siguiente**, con los horarios disponibles del tutor.
5. El estudiante selecciona un bloque horario libre (día y hora).
6. El estudiante escribe un mensaje describiendo qué necesita de la tutoría (mínimo 10 caracteres, máximo 500).
7. El estudiante confirma el envío de la solicitud.
8. El sistema valida los datos y registra la solicitud en estado **Pendiente**.
9. El sistema muestra un mensaje de confirmación y redirige al perfil del tutor indicando que la solicitud quedó pendiente de respuesta.

## Flujos alternativos y de excepción

### FA-1 — Materia preseleccionada desde la búsqueda

- **Punto de divergencia:** paso 1.
- El estudiante llegó filtrando tutores por una materia concreta.
- El sistema muestra esa materia ya seleccionada y el estudiante continúa desde el paso 4.

### FA-2 — Cambio de semana en el calendario

- **Punto de divergencia:** paso 4.
- El estudiante alterna entre la semana actual y la semana siguiente para ver más horarios.
- Continúa en el paso 5 cuando elige un horario.

### FA-3 — Horario no disponible en pantalla

- **Punto de divergencia:** paso 4.
- Algunos bloques aparecen como **No disponible** u **Ocupados** porque ya tienen una solicitud pendiente o aceptada de otro estudiante.
- El estudiante debe elegir otro horario libre (vuelve al paso 5).

### FE-1 — Sesión no iniciada

- **Punto de divergencia:** paso 1.
- El estudiante no está autenticado.
- El sistema lo redirige al inicio de sesión y el caso de uso termina sin crear solicitud.

### FE-2 — Tutor no encontrado o inactivo

- **Punto de divergencia:** paso 1.
- El tutor no existe o no está activo.
- El sistema muestra un error y redirige a la búsqueda de tutores.

### FE-3 — Datos incompletos

- **Punto de divergencia:** paso 7.
- Falta materia, horario, fecha o mensaje.
- El sistema muestra el mensaje *«Completa materia, horario y mensaje para continuar.»* y el estudiante corrige la información.

### FE-4 — Mensaje inválido

- **Punto de divergencia:** paso 7.
- El mensaje está vacío, tiene menos de 10 caracteres o supera 500.
- El sistema informa el requisito y el estudiante ajusta el mensaje.

### FE-5 — Materia no permitida para el semestre del estudiante

- **Punto de divergencia:** paso 8.
- La materia es de un semestre superior al del estudiante.
- El sistema muestra *«Esa materia no está habilitada para tu semestre.»*

### FE-6 — El tutor no dicta la materia seleccionada

- **Punto de divergencia:** paso 8.
- El sistema detecta que la materia no está en la oferta del tutor.
- Se muestra *«El tutor no ofrece esa materia.»*

### FE-7 — Horario fuera del rango permitido

- **Punto de divergencia:** paso 8.
- La fecha no corresponde a la semana actual o siguiente, el día no coincide con el horario elegido, o la franja ya pasó (incluido el mismo día si la hora de inicio ya transcurrió).
- El sistema muestra un mensaje acorde (por ejemplo: *«Solo puedes solicitar horarios de la semana actual o la siguiente.»* o *«No puedes solicitar un horario que ya pasó.»*).

### FE-8 — Horario ya reservado

- **Punto de divergencia:** paso 8.
- Otro estudiante ya tiene una solicitud pendiente o aceptada para ese mismo horario y fecha.
- El sistema muestra *«Ese horario ya fue solicitado por otro estudiante.»* y el estudiante debe elegir otro bloque.

### FE-9 — Error inesperado al enviar

- **Punto de divergencia:** paso 8.
- Falla el registro de la solicitud por un problema del sistema.
- El sistema muestra *«No se pudo enviar la solicitud. Intenta de nuevo.»* y no se crea la solicitud.

---

**Caso de uso relacionado:** CU-10 — Responder a una solicitud (cuando el tutor acepta o rechaza).  
**Caso de uso previo sugerido:** CU-06 — Buscar tutores / CU-07 — Ver perfil de un tutor.
