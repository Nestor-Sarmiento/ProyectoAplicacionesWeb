# CU-08 — Solicitar una tutoría

## Actores

- **Estudiante** (actor principal): usuario autenticado que necesita apoyo en una materia y solicita una sesión con un tutor.
- **Tutor**: no interactúa en este caso de uso; solo queda como destinatario de la solicitud en estado pendiente (su interacción ocurre en CU-10).

## Descripción

Partiendo del **perfil del tutor**, el estudiante inicia la solicitud de tutoría, completa el formulario (materia, horario disponible en la semana actual o siguiente, y mensaje) y lo envía. El sistema valida los datos, registra la solicitud en estado **Pendiente** y confirma el resultado al estudiante.

La búsqueda del tutor (con o sin filtro de materia) corresponde a casos de uso previos; este caso de uso comienza cuando el estudiante ya está visualizando el perfil.

## Precondiciones

1. El estudiante tiene una sesión activa en OwlShare.
2. El estudiante se encuentra visualizando el **perfil del tutor** (CU-07).
3. El tutor está activo, ofrece al menos una materia que el estudiante puede solicitar según su semestre, y tiene horarios de disponibilidad configurados.
4. El horario que se elija deberá pertenecer a la **semana actual** o a la **semana siguiente**, no haber pasado y no estar ocupado por otra solicitud pendiente o aceptada del mismo tutor.

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

1. Desde el perfil del tutor, el estudiante elige **Solicitar tutoría**.
2. El sistema muestra el formulario de solicitud: materias elegibles, calendario de horarios (semana actual y siguiente) y campo de mensaje.
3. El estudiante completa el formulario (materia, bloque horario libre y mensaje de 10 a 500 caracteres) y confirma el envío.
4. El sistema valida los datos de la solicitud.
5. El sistema registra la solicitud en estado **Pendiente**.
6. El sistema muestra un mensaje de confirmación y redirige al perfil del tutor indicando que la solicitud quedó pendiente de respuesta.

## Flujos alternativos y de excepción

### 2.1 — Materia ya preseleccionada

2.1.1. En el paso 2, el perfil del tutor se abrió con una materia de contexto (por ejemplo, tras filtrar en la búsqueda).  
2.1.2. El sistema muestra esa materia ya seleccionada en el formulario.  
2.1.3. El flujo continúa en el paso 3.

### 2.2 — Cambio de semana en el calendario

2.2.1. En el paso 2, el estudiante alterna entre la semana actual y la semana siguiente.  
2.2.2. El sistema actualiza el calendario con los horarios de la semana elegida.  
2.2.3. El flujo continúa en el paso 3.

### 2.3 — Horario no disponible en pantalla

2.3.1. En el paso 2, el sistema marca algunos bloques como **No disponible** porque la fecha ya pasó o ya tienen una solicitud pendiente o aceptada.  
2.3.2. El estudiante no puede seleccionar esos bloques.  
2.3.3. El flujo continúa en el paso 3.

### 2.4 — El tutor ya no permite solicitar

2.4.1. En el paso 2, el tutor dejó de estar activo, no tiene materias elegibles o no tiene horarios disponibles.  
2.4.2. El sistema no permite completar la solicitud e informa la situación.  
2.4.3. El caso de uso termina sin crear solicitud.

### 4.1 — Datos incompletos

4.1.1. En el paso 4, falta materia, horario, fecha o mensaje.  
4.1.2. El sistema muestra *«Completa materia, horario y mensaje para continuar.»*  
4.1.3. El flujo regresa al paso 3.

### 4.2 — Mensaje inválido

4.2.1. En el paso 4, el mensaje está vacío, tiene menos de 10 caracteres o supera 500.  
4.2.2. El sistema informa el requisito del mensaje.  
4.2.3. El flujo regresa al paso 3.

---

**Caso de uso previo:** CU-07 — Ver perfil de un tutor.  
**Caso de uso relacionado:** CU-10 — Responder a una solicitud (cuando el tutor acepta o rechaza).

## Diagrama de casos de uso

```plantuml
@startuml CU-08-Solicitar-tutoria
left to right direction
skinparam packageStyle rectangle

actor "Estudiante" as Estudiante
actor "Tutor" as Tutor

rectangle OwlShare {
  usecase "CU-07\nVer perfil de un tutor" as CU07
  usecase "CU-08\nSolicitar una tutoría" as CU08
  usecase "CU-10\nResponder a una solicitud" as CU10
}

Estudiante --> CU07
Estudiante --> CU08
Tutor --> CU10

CU08 ..> CU07 : <<precede>>
CU10 ..> CU08 : <<precede>>

note right of CU08
  Actor principal: Estudiante
  El Tutor no interactúa en este CU;
  la solicitud queda Pendiente
  hasta CU-10
end note

note bottom of CU07
  Precondición de CU-08:
  el estudiante ya visualiza el perfil
end note

@enduml
```
