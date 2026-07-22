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
4.1.2. El sistema muestra un mensaje de error.  
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

@enduml
```

## Modelo de dominio

```plantuml
@startuml OwlShare-ModeloDominio
skinparam backgroundColor white
skinparam classAttributeIconSize 0
skinparam class {
  BackgroundColor white
  BorderColor black
  ArrowColor black
}
hide circle
hide empty methods

abstract class Usuario {
  email
  nombre
  apellido
  activo
}

class Estudiante {
  semestre
}

class Tutor {
  semestre
}

class Carrera {
  codigo
  nombre
}

class Asignatura {
  codigo
  nombre
  semestre
}

class Disponibilidad {
  diaSemana
  horaInicio
  horaFin
  activo
}

class SolicitudTutoria {
  fechaSesion
  mensaje
  fechaCreacion
}

abstract class EstadoSolicitud

class Pendiente
class Aceptada
class Rechazada
class Cancelada

class SesionTutoria {
  completada
  calificacion
  comentario
}

Usuario <|-- Estudiante
Usuario <|-- Tutor

EstadoSolicitud <|-- Pendiente
EstadoSolicitud <|-- Aceptada
EstadoSolicitud <|-- Rechazada
EstadoSolicitud <|-- Cancelada

Carrera "1" o-- "1..*" Asignatura : contiene >

Estudiante "*" --> "1" Carrera : pertenece a >
Tutor "*" --> "1" Carrera : pertenece a >

Tutor "0..*" -- "1..*" Asignatura : dicta >
Tutor "1" o-- "0..*" Disponibilidad : ofrece >

Estudiante "1" --> "*" SolicitudTutoria : realiza >
Tutor "1" --> "*" SolicitudTutoria : recibe >
Asignatura "1" --> "*" SolicitudTutoria : es solicitada en >
Disponibilidad "1" --> "*" SolicitudTutoria : es usada en >

SolicitudTutoria "*" --> "1" EstadoSolicitud : se encuentra en >

SolicitudTutoria "1" --> "0..1" SesionTutoria : genera >
SesionTutoria "*" --> "1" Tutor : involucra a >
SesionTutoria "*" --> "1" Estudiante : involucra a >

@enduml
```

## Diagrama de robustez

```plantuml
@startuml CU-08-Robustez
left to right direction
skinparam backgroundColor white
skinparam shadowing false
skinparam ArrowColor #424242
skinparam actor {
  BackgroundColor white
  BorderColor black
}
skinparam boundary {
  BackgroundColor #E3F2FD
  BorderColor #1565C0
}
skinparam control {
  BackgroundColor #FFF8E1
  BorderColor #F9A825
}
skinparam entity {
  BackgroundColor #E8F5E9
  BorderColor #2E7D32
}

actor "Estudiante" as Actor

control "Estudiante" as Ctrl

boundary "Detalle-Tutor" as Perfil
boundary "Solicitar-Tutoria" as Form

entity "Estudiante" as EntEst
entity "Tutor" as EntTutor
entity "Asignatura" as EntAsig
entity "Disponibilidad" as EntDisp
entity "SolicitudTutoria" as EntSol

Actor --> Ctrl : <color:#212121>1. Elige Solicitar tutoría</color>\n<color:#212121>3. Completa el formulario y confirma el envío</color>\n<color:#1565C0>2.1.1 Indica materia de contexto desde el perfil</color>\n<color:#00838F>2.2.1 Alterna entre semana actual y siguiente</color>\n<color:#6A1B9A>2.3.2 No puede seleccionar bloques no disponibles</color>

Ctrl --> Perfil : <color:#212121>6. Muestra confirmación y perfil con solicitud pendiente</color>

Ctrl --> Form : <color:#212121>2. Muestra el formulario (materias, calendario y mensaje)</color>\n<color:#1565C0>2.1.2 Muestra la materia ya seleccionada</color>\n<color:#00838F>2.2.2 Actualiza el calendario con la semana elegida</color>\n<color:#6A1B9A>2.3.1 Marca bloques como No disponible</color>\n<color:#E65100>2.4.2 No permite completar e informa la situación</color>\n<color:#C62828>4.1.2 Muestra mensaje de error (datos incompletos)</color>\n<color:#AD1457>4.2.2 Informa el requisito del mensaje</color>

Ctrl --> EntTutor : <color:#212121>2. Carga los datos del tutor seleccionado</color>\n<color:#E65100>2.4.1 Detecta que el tutor ya no permite solicitar</color>\n<color:#212121>4. Valida que el tutor siga activo</color>\n<color:#212121>5. Asocia el tutor a la solicitud</color>

Ctrl --> EntAsig : <color:#212121>2. Carga materias elegibles</color>\n<color:#1565C0>2.1.1 Usa la materia de contexto del perfil</color>\n<color:#1565C0>2.1.2 Preselecciona esa materia en el formulario</color>\n<color:#E65100>2.4.1 Detecta ausencia de materias elegibles</color>\n<color:#212121>4. Valida la materia elegida</color>\n<color:#212121>5. Asocia la asignatura a la solicitud</color>

Ctrl --> EntDisp : <color:#212121>2. Carga horarios (semana actual y siguiente)</color>\n<color:#00838F>2.2.2 Entrega horarios de la semana elegida</color>\n<color:#E65100>2.4.1 Detecta ausencia de horarios disponibles</color>\n<color:#212121>4. Valida el bloque horario y la fecha</color>\n<color:#212121>5. Asocia la disponibilidad a la solicitud</color>

Ctrl --> EntSol : <color:#6A1B9A>2.3.1 Consulta slots ocupados (pendiente o aceptada)</color>\n<color:#212121>4. Valida los datos de la solicitud</color>\n<color:#C62828>4.1.1 Detecta falta de materia, horario, fecha o mensaje</color>\n<color:#AD1457>4.2.1 Detecta mensaje vacío, corto o demasiado largo</color>\n<color:#212121>5. Registra la solicitud en estado Pendiente</color>

Ctrl --> EntEst : <color:#212121>2. Filtra materias según el semestre del estudiante</color>\n<color:#212121>5. Asocia el estudiante a la solicitud</color>

@enduml
```

## Diagrama de secuencia de diseño

```plantuml
@startuml CU-08-SecuenciaDiseno
skinparam backgroundColor white
skinparam shadowing false
skinparam sequenceMessageAlign center
skinparam responseMessageBelowArrow true
skinparam actor {
  BackgroundColor white
  BorderColor black
}
skinparam boundary {
  BackgroundColor #E3F2FD
  BorderColor #1565C0
}
skinparam control {
  BackgroundColor #FFF8E1
  BorderColor #F9A825
}
skinparam entity {
  BackgroundColor #E8F5E9
  BorderColor #2E7D32
}

actor "Estudiante" as Actor
control "Estudiante" as Ctrl
boundary "Detalle-Tutor" as Perfil
boundary "Solicitar-Tutoria" as Form
entity "Estudiante" as EntEst
entity "Tutor" as EntTutor
entity "Asignatura" as EntAsig
entity "Disponibilidad" as EntDisp
entity "SolicitudTutoria" as EntSol

== Mostrar formulario ==

Actor -> Perfil : visualiza perfil (precondición CU-07)
Actor -> Ctrl : solicitar-tutoria\n(tutorId: Long, asignaturaId: Long)
activate Ctrl

Ctrl -> Ctrl : ruteador(): void
Ctrl -> Ctrl : mostrarSolicitarTutoria(): void
Ctrl -> Ctrl : requerirEstudiante(): Estudiante

Ctrl -> EntTutor : buscarPorIdConMaterias(Long id): Tutor
activate EntTutor
EntTutor --> Ctrl : Tutor
deactivate EntTutor

alt Tutor es null o inactivo
  Ctrl -> Form : mostrar(): void
else tutor activo
  Ctrl -> EntEst : getSemestre(): int
  activate EntEst
  EntEst --> Ctrl : int
  deactivate EntEst

  Ctrl -> EntAsig : filtrarElegibles(Tutor, int semestre): List<Asignatura>
  activate EntAsig
  EntAsig --> Ctrl : List<Asignatura>
  deactivate EntAsig

  opt asignaturaId presente
    Ctrl -> EntAsig : obtenerPorId(Long id): Asignatura
    activate EntAsig
    EntAsig --> Ctrl : Asignatura
    deactivate EntAsig
  end

  Ctrl -> EntDisp : listarPorTutor(Long tutorId): List<Disponibilidad>
  activate EntDisp
  EntDisp --> Ctrl : List<Disponibilidad>
  deactivate EntDisp

  Ctrl -> EntSol : clavesSlotsOcupados(Long tutorId,\nLocalDate inicio, LocalDate fin): Set<String>
  activate EntSol
  EntSol --> Ctrl : Set<String>
  deactivate EntSol

  Ctrl -> Ctrl : construirSemanasJson(LocalDate, LocalDate): String

  Ctrl -> Form : mostrar(): void
  activate Form
  Form --> Actor : formulario
  deactivate Form
  deactivate Ctrl
end

opt cambio de semana
  Actor -> Form : seleccionarSemana(int indice): void
  activate Form
  Form -> Form : actualizarCalendario(String semanasJson,\nString slotsDisponibles, String slotsOcupados): void
  Form --> Actor : calendario
  deactivate Form
end

== Enviar solicitud ==

Actor -> Ctrl : enviar-solicitud\n(tutorId: Long, asignaturaId: Long,\ndisponibilidadId: Long, fecha: String, mensaje: String)
activate Ctrl

Ctrl -> Ctrl : ruteador(): void
Ctrl -> Ctrl : enviarSolicitud(): void
Ctrl -> Ctrl : requerirEstudiante(): Estudiante

alt datos incompletos
  Ctrl -> Form : mostrar(): void
  activate Form
  Form --> Actor : error datos incompletos
  deactivate Form
else datos presentes
  alt mensaje inválido
    Ctrl -> EntSol : validarMensaje(String mensaje): void
    activate EntSol
    EntSol --> Ctrl : IllegalArgumentException
    deactivate EntSol
    Ctrl -> Form : mostrar(): void
    activate Form
    Form --> Actor : error mensaje
    deactivate Form
  else mensaje válido
    Ctrl -> EntEst : buscarPorId(Long id): Estudiante
    activate EntEst
    EntEst --> Ctrl : Estudiante
    deactivate EntEst

    Ctrl -> EntTutor : buscarPorIdConMaterias(Long id): Tutor
    activate EntTutor
    EntTutor --> Ctrl : Tutor
    deactivate EntTutor

    Ctrl -> EntAsig : buscarPorId(Long id): Asignatura
    activate EntAsig
    EntAsig --> Ctrl : Asignatura
    deactivate EntAsig

    Ctrl -> EntDisp : buscarPorId(Long id): Disponibilidad
    activate EntDisp
    EntDisp --> Ctrl : Disponibilidad
    deactivate EntDisp

    Ctrl -> EntSol : validar(Tutor, Asignatura, Disponibilidad,\nLocalDate fecha, String mensaje): void
    activate EntSol
    EntSol --> Ctrl : void
    deactivate EntSol

    Ctrl -> EntSol : contarActivasPorSlot(Long tutorId,\nLong disponibilidadId, LocalDate fecha): long
    activate EntSol
    EntSol --> Ctrl : long
    deactivate EntSol

    Ctrl -> EntSol : crear(Estudiante, Tutor, Asignatura,\nDisponibilidad, LocalDate, String): SolicitudTutoria
    activate EntSol
    EntSol -> EntSol : setEstado(EstadoSolicitud.PENDIENTE): void
    EntSol -> EntSol : guardar(): void
    EntSol --> Ctrl : SolicitudTutoria
    deactivate EntSol

    Ctrl -> Ctrl : ruteador(): void
    Ctrl -> Ctrl : detalleTutor(): void

    Ctrl -> EntTutor : buscarPorIdConMaterias(Long id): Tutor
    activate EntTutor
    EntTutor --> Ctrl : Tutor
    deactivate EntTutor

    Ctrl -> EntDisp : listarPorTutor(Long tutorId): List<Disponibilidad>
    activate EntDisp
    EntDisp --> Ctrl : List<Disponibilidad>
    deactivate EntDisp

    Ctrl -> Perfil : mostrar(): void
    activate Perfil
    Perfil --> Actor : confirmación
    deactivate Perfil
  end
end

deactivate Ctrl

@enduml
```

## Diagrama de clases de diseño

```plantuml
@startuml CU-08-DisenoClases
skinparam backgroundColor white
skinparam shadowing false
skinparam classAttributeIconSize 0
skinparam class {
  BackgroundColor white
  BorderColor black
}
hide empty members

class "Detalle-Tutor" {
  + mostrar(): void
}

class "Solicitar-Tutoria" {
  + mostrar(): void
  + seleccionarSemana(indice: int): void
  + actualizarCalendario(semanasJson: String, slotsDisponibles: String, slotsOcupados: String): void
}

class EstudianteController {
  - tutorDAO: TutorDAO
  - disponibilidadService: DisponibilidadService
  - solicitudService: SolicitudService
  --
  + doGet(req: HttpServletRequest, resp: HttpServletResponse): void
  + doPost(req: HttpServletRequest, resp: HttpServletResponse): void
  - ruteador(req: HttpServletRequest, resp: HttpServletResponse): void
  - mostrarSolicitarTutoria(req: HttpServletRequest, resp: HttpServletResponse): void
  - enviarSolicitud(req: HttpServletRequest, resp: HttpServletResponse): void
  - detalleTutor(req: HttpServletRequest, resp: HttpServletResponse): void
  - requerirEstudiante(req: HttpServletRequest, resp: HttpServletResponse): Estudiante
  - filtrarMateriasParaEstudiante(tutor: Tutor, estudiante: Estudiante): List<Asignatura>
  - listarHorariosOrdenados(tutorId: Long): List<Disponibilidad>
  - construirSemanasJson(lunesActual: LocalDate, lunesSiguiente: LocalDate): String
  - construirSemanaJson(indice: int, etiqueta: String, lunes: LocalDate): String
  - escaparJson(valor: String): String
  - parseLong(valor: String): Long
  - parseFecha(valor: String): LocalDate
  - encode(valor: String): String
}

class SolicitudService {
  - solicitudDAO: SolicitudDAO
  - tutorDAO: TutorDAO
  - asignaturaDAO: AsignaturaDAO
  - disponibilidadDAO: DisponibilidadDAO
  - usuarioDAO: UsuarioDAO
  --
  + {static} claveSlot(disponibilidadId: Long, fecha: LocalDate): String
  + crear(estudianteId: Long, tutorId: Long, asignaturaId: Long, disponibilidadId: Long, fechaSesion: LocalDate, mensaje: String): SolicitudTutoria
  + clavesSlotsOcupados(tutorId: Long, inicioSemana: LocalDate, finSemana: LocalDate): Set<String>
  + listarPorTutor(tutorId: Long): List<SolicitudTutoria>
  + listarPorEstudiante(estudianteId: Long): List<SolicitudTutoria>
  + listarProximasSesionesEstudiante(estudianteId: Long): List<SolicitudTutoria>
  + listarProximasSesionesTutor(tutorId: Long): List<SolicitudTutoria>
  + responderSolicitud(solicitudId: Long, tutorId: Long, nuevoEstado: EstadoSolicitud): SolicitudTutoria
  + cancelarPorEstudiante(solicitudId: Long, estudianteId: Long): void
  + cancelarPorTutor(solicitudId: Long, tutorId: Long): void
}

class DisponibilidadService {
  - disponibilidadDAO: DisponibilidadDAO
  --
  + listarPorTutor(tutorId: Long): List<Disponibilidad>
  + slotsComoCadena(tutorId: Long): String
  + listarSlotKeys(tutorId: Long): List<String>
  + reemplazarSlots(tutorId: Long, slotsCsv: String): void
}

class TutorDAO {
  + buscarPorIdConMaterias(id: Long): Tutor
  + actualizar(tutor: Tutor): void
  + buscar(carreraId: Long, asignaturaId: Long): List<Tutor>
  + buscar(carreraId: Long, asignaturaId: Long, semestreMax: Integer): List<Tutor>
  + buscarAsignaturasPorIds(asignaturaIds: Set<Long>): List<Asignatura>
}

class SolicitudDAO {
  + guardar(solicitud: SolicitudTutoria): void
  + buscarPorId(id: Long): SolicitudTutoria
  + buscarPorIdConRelaciones(id: Long): SolicitudTutoria
  + actualizar(solicitud: SolicitudTutoria): void
  + contarActivasPorSlot(tutorId: Long, disponibilidadId: Long, fecha: LocalDate): long
  + listarActivasPorTutorEnRango(tutorId: Long, inicioSemana: LocalDate, finSemana: LocalDate): List<SolicitudTutoria>
  + listarPorTutor(tutorId: Long): List<SolicitudTutoria>
  + listarPorEstudiante(estudianteId: Long): List<SolicitudTutoria>
  + listarAceptadasDesdeHoy(estudianteId: Long, tutorId: Long): List<SolicitudTutoria>
}

class DisponibilidadDAO {
  + buscarPorId(id: Long): Disponibilidad
  + listarPorTutor(tutorId: Long): List<Disponibilidad>
  + sincronizarSlots(tutorId: Long, slotsDeseados: Set<String>): void
  + guardar(disponibilidad: Disponibilidad): void
  + guardarTodos(disponibilidades: List<Disponibilidad>): void
}

class UsuarioDAO {
  + buscarPorEmail(email: String): Usuario
  + buscarPorId(id: Long): Optional<Usuario>
  + contar(): long
  + guardar(usuario: Usuario): void
  + actualizar(usuario: Usuario): void
}

class AsignaturaDAO {
  + listarPorCarrera(carreraId: Long): List<Asignatura>
  + listarPorCarreraHastaSemestre(carreraId: Long, semestreMaximo: int): List<Asignatura>
  + listarAprobadasParaTutor(carreraId: Long, semestreTutor: int): List<Asignatura>
  + buscarPorId(id: Long): Asignatura
  + buscarPorIds(ids: List<Long>): List<Asignatura>
}

abstract class Usuario {
  - id: Long
  - email: String
  - password: String
  - nombre: String
  - segundoNombre: String
  - apellido: String
  - segundoApellido: String
  - activo: boolean
  --
  + getId(): Long
  + setId(id: Long): void
  + getEmail(): String
  + setEmail(email: String): void
  + getPassword(): String
  + setPassword(password: String): void
  + getNombre(): String
  + setNombre(nombre: String): void
  + getSegundoNombre(): String
  + setSegundoNombre(segundoNombre: String): void
  + getApellido(): String
  + setApellido(apellido: String): void
  + getSegundoApellido(): String
  + setSegundoApellido(segundoApellido: String): void
  + isActivo(): boolean
  + setActivo(activo: boolean): void
  + getNombreCompleto(): String
}

class Estudiante {
  - semestre: int
  - carrera: Carrera
  --
  + getSemestre(): int
  + setSemestre(semestre: int): void
  + getCarrera(): Carrera
  + setCarrera(carrera: Carrera): void
}

class Tutor {
  - semestre: int
  - carrera: Carrera
  - materias: Set<Asignatura>
  --
  + getSemestre(): int
  + setSemestre(semestre: int): void
  + getCarrera(): Carrera
  + setCarrera(carrera: Carrera): void
  + getMaterias(): Set<Asignatura>
  + setMaterias(materias: Set<Asignatura>): void
}

class Carrera {
  - id: Long
  - codigo: String
  - nombre: String
  - asignaturas: List<Asignatura>
  --
  + getId(): Long
  + setId(id: Long): void
  + getCodigo(): String
  + setCodigo(codigo: String): void
  + getNombre(): String
  + setNombre(nombre: String): void
  + getAsignaturas(): List<Asignatura>
  + setAsignaturas(asignaturas: List<Asignatura>): void
}

class Asignatura {
  - id: Long
  - codigo: String
  - nombre: String
  - semestre: int
  - carrera: Carrera
  --
  + getId(): Long
  + setId(id: Long): void
  + getCodigo(): String
  + setCodigo(codigo: String): void
  + getNombre(): String
  + setNombre(nombre: String): void
  + getSemestre(): int
  + setSemestre(semestre: int): void
  + getCarrera(): Carrera
  + setCarrera(carrera: Carrera): void
}

class Disponibilidad {
  - id: Long
  - tutor: Tutor
  - diaSemana: DiaSemana
  - horaInicio: String
  - horaFin: String
  - activo: boolean
  --
  + getId(): Long
  + setId(id: Long): void
  + getTutor(): Tutor
  + setTutor(tutor: Tutor): void
  + getDiaSemana(): DiaSemana
  + setDiaSemana(diaSemana: DiaSemana): void
  + getHoraInicio(): String
  + setHoraInicio(horaInicio: String): void
  + getHoraFin(): String
  + setHoraFin(horaFin: String): void
  + isActivo(): boolean
  + setActivo(activo: boolean): void
  + toSlotKey(): String
}

class SolicitudTutoria {
  - id: Long
  - estudiante: Estudiante
  - tutor: Tutor
  - asignatura: Asignatura
  - disponibilidad: Disponibilidad
  - fechaSesion: LocalDate
  - mensaje: String
  - estado: EstadoSolicitud
  - fechaCreacion: LocalDateTime
  --
  + getId(): Long
  + setId(id: Long): void
  + getEstudiante(): Estudiante
  + setEstudiante(estudiante: Estudiante): void
  + getTutor(): Tutor
  + setTutor(tutor: Tutor): void
  + getAsignatura(): Asignatura
  + setAsignatura(asignatura: Asignatura): void
  + getDisponibilidad(): Disponibilidad
  + setDisponibilidad(disponibilidad: Disponibilidad): void
  + getFechaSesion(): LocalDate
  + setFechaSesion(fechaSesion: LocalDate): void
  + getMensaje(): String
  + setMensaje(mensaje: String): void
  + getEstado(): EstadoSolicitud
  + setEstado(estado: EstadoSolicitud): void
  + getFechaCreacion(): LocalDateTime
  + setFechaCreacion(fechaCreacion: LocalDateTime): void
}

enum EstadoSolicitud {
  PENDIENTE
  ACEPTADA
  RECHAZADA
  CANCELADA
  etiqueta: String
  --
  + getEtiqueta(): String
}

enum DiaSemana {
  LUNES
  MARTES
  MIERCOLES
  JUEVES
  VIERNES
  SABADO
  DOMINGO
  etiqueta: String
  --
  + getEtiqueta(): String
  + toDayOfWeek(): DayOfWeek
  + {static} from(dayOfWeek: DayOfWeek): DiaSemana
  + {static} parse(valor: String): DiaSemana
}

EstudianteController ..> "Detalle-Tutor" : muestra >
EstudianteController ..> "Solicitar-Tutoria" : muestra >
EstudianteController --> SolicitudService : delega en >
EstudianteController --> DisponibilidadService : consulta >
EstudianteController --> TutorDAO : consulta >

SolicitudService --> SolicitudDAO : persiste mediante >
SolicitudService --> TutorDAO : consulta >
SolicitudService --> AsignaturaDAO : consulta >
SolicitudService --> DisponibilidadDAO : consulta >
SolicitudService --> UsuarioDAO : consulta >

DisponibilidadService --> DisponibilidadDAO : persiste mediante >

Usuario <|-- Estudiante : es un >
Usuario <|-- Tutor : es un >

Estudiante "*" --> "1" Carrera : pertenece a >
Tutor "*" --> "1" Carrera : pertenece a >
Carrera "1" o-- "1..*" Asignatura : contiene >
Tutor "0..*" -- "1..*" Asignatura : dicta >
Tutor "1" o-- "0..*" Disponibilidad : ofrece >
Disponibilidad --> DiaSemana : se agenda en >

Estudiante "1" --> "*" SolicitudTutoria : realiza >
Tutor "1" --> "*" SolicitudTutoria : recibe >
Asignatura "1" --> "*" SolicitudTutoria : es solicitada en >
Disponibilidad "1" --> "*" SolicitudTutoria : es usada en >
SolicitudTutoria --> EstadoSolicitud : se encuentra en >

TutorDAO ..> Tutor : gestiona >
SolicitudDAO ..> SolicitudTutoria : gestiona >
DisponibilidadDAO ..> Disponibilidad : gestiona >
AsignaturaDAO ..> Asignatura : gestiona >
UsuarioDAO ..> Usuario : gestiona >

@enduml
```
