# Glosario de Lenguaje Ubicuo
## Sistema de Triage y Gestión de Solicitudes Académicas
### Programa de Ingeniería de Sistemas y Computación — Universidad del Quindío
### Curso: Programación Avanzada

---

## Introducción

Este glosario define los términos clave del dominio del Sistema de Triage y Gestión de Solicitudes Académicas. Su propósito es establecer un **lenguaje compartido** entre estudiantes, docentes, administrativos y el código fuente, eliminando ambigüedades y asegurando que todos los involucrados hablen el mismo idioma.

---

## Términos del Dominio

### Entidades

| Nombre | Definición | Clasificación |
|--------|-----------|---------------|
| **Solicitud** | Petición formal registrada por cualquier usuario ante el programa académico. Tiene identidad propia, un ciclo de vida definido y concentra las reglas más importantes del dominio. Cada solicitud es única aunque tenga los mismos datos que otra. | Entidad / Agregado Raíz |
| **Usuario** | Persona que interactúa con el sistema. Puede ser un Estudiante, Docente o Administrativo. Tiene identidad propia y un rol que determina qué acciones puede realizar dentro del sistema. | Entidad |

---

### Value Objects

| Nombre | Definición | Clasificación |
|--------|-----------|---------------|
| **Email** | Dirección de correo electrónico de un usuario. Se define únicamente por su valor y debe tener un formato válido. Es inmutable: no puede cambiar una vez creado. | Value Object |
| **CodigoSolicitud** | Identificador único generado automáticamente para cada solicitud al momento de su registro. Sigue el formato `SOL-{timestamp}`. Es inmutable y no puede estar vacío. | Value Object |
| **Prioridad** | Nivel de urgencia calculado automáticamente por el sistema para una solicitud. Puede ser `ALTA`, `MEDIA` o `BAJA`. Se determina según el tipo de solicitud y el tiempo de espera. Es inmutable. | Value Object |
| **EstadoSolicitud** | Representa el punto del ciclo de vida en que se encuentra una solicitud. Los estados válidos son: `CLASIFICACION`, `PENDIENTE`, `EN_PROCESO`, `ATENDIDA`, `CERRADA`, `RECHAZADA` y `CANCELADA`. Las transiciones entre estados están controladas por el agregado. | Value Object |
| **TipoSolicitud** | Categoría que clasifica el propósito de una solicitud. Puede ser: `HOMOLOGACION`, `CANCELACION`, `SOLICITUD_CUPO` u `OTRO`. Se define por su valor y es inmutable. | Value Object |
| **TipoUsuario** | Rol que determina las acciones que un usuario puede realizar en el sistema. Puede ser `ESTUDIANTE`, `DOCENTE` o `ADMINISTRATIVO`. Es inmutable. | Value Object |
| **TipoNotificacion** | Categoría del evento que dispara una notificación. Puede ser: `NUEVA_SOLICITUD`, `ASIGNACION`, `CAMBIO_ESTADO` o `CIERRE`. | Value Object |
| **EventoHistorial** | Registro inmutable de una acción relevante ocurrida sobre una solicitud. Incluye la descripción del evento, el estado resultante, quién lo realizó y la fecha. Solo puede ser creado por el agregado `Solicitud`. No permite duplicados. | Value Object |

---

### Agregados

| Nombre | Definición | Clasificación |
|--------|-----------|---------------|
| **Solicitud** | Agregado raíz que agrupa y protege la consistencia de todos los conceptos relacionados con una solicitud académica. Controla el acceso al historial de eventos y garantiza que las invariantes del dominio se cumplan en todo momento. Toda modificación relevante debe pasar obligatoriamente por este agregado. | Agregado Raíz |

---

### Servicios de Dominio

| Nombre | Definición | Clasificación |
|--------|-----------|---------------|
| **GestorSolicitudService** | Servicio de dominio que gestiona las reglas de roles sobre las acciones del ciclo de vida de una solicitud. Determina quién puede clasificar, poner en revisión, atender, rechazar, cerrar o cancelar una solicitud. No contiene lógica técnica, solo lógica del negocio. | Servicio de Dominio |
| **AsignadorPrioridadService** | Servicio de dominio que calcula automáticamente la prioridad de una solicitud según su tipo y el tiempo transcurrido desde su creación. Aplica reglas de envejecimiento para evitar que solicitudes de baja prioridad queden indefinidamente sin atender. | Servicio de Dominio |
| **NotificadorSolicitudes** | Servicio de dominio que determina a quién se debe notificar según el tipo de evento ocurrido sobre una solicitud. Coordina información de múltiples entidades sin pertenecer naturalmente a ninguna de ellas. | Servicio de Dominio |

---

### Conceptos Adicionales del Dominio

| Nombre | Definición |
|--------|-----------|
| **Triage** | Proceso de clasificación y priorización automática de solicitudes académicas según su tipo y urgencia, con el objetivo de asignarlas correctamente y atenderlas en el orden adecuado. |
| **Ciclo de vida de una solicitud** | Secuencia de estados por los que pasa una solicitud desde su registro hasta su cierre o cancelación: `CLASIFICACION → PENDIENTE → EN_PROCESO → ATENDIDA → CERRADA`. Con salidas alternativas hacia `RECHAZADA` o `CANCELADA`. |
| **Historial auditable** | Registro cronológico e inmutable de todas las acciones relevantes realizadas sobre una solicitud. Implementado con `LinkedHashSet` para garantizar orden de inserción y ausencia de duplicados. |
| **Invariante** | Regla de negocio que debe ser siempre verdadera. El agregado `Solicitud` garantiza que sus invariantes se mantengan en todo momento, lanzando excepciones cuando se intenta violarlas. |
| **Clasificación** | Acción realizada por un administrativo que asigna un tipo a una solicitud. La prioridad es calculada automáticamente por el `AsignadorPrioridadService`. Cambia el estado de `CLASIFICACION` a `PENDIENTE`. |
| **Envejecimiento de prioridad** | Mecanismo por el cual la prioridad de una solicitud sube automáticamente si lleva más de 3 días sin atenderse, y pasa directamente a `ALTA` si lleva más de 7 días. Evita la inanición de solicitudes de baja prioridad. |
| **Responsable** | Usuario de tipo `ADMINISTRATIVO` asignado a una solicitud al momento de ponerla en revisión. Es obligatorio para poder atender y cerrar la solicitud. |
| **Solicitante** | Cualquier usuario (estudiante, docente o administrativo) que registra una solicitud en el sistema. Es el único que puede cancelar su propia solicitud. |

---

## Acciones del Dominio

Estas son las acciones que el sistema puede realizar, expresadas en el lenguaje del dominio:

| Acción | Descripción | Quién la ejecuta |
|--------|------------|-----------------|
| `registrar()` | Crea una nueva solicitud en estado `CLASIFICACION` | Cualquier usuario |
| `clasificar(tipo)` | Asigna el tipo a una solicitud; el sistema calcula la prioridad automáticamente | Administrativo |
| `enRevision()` | Asigna un responsable y pone la solicitud en proceso | Administrativo |
| `atendida()` | Indica que la solicitud ha sido atendida por el responsable | Administrativo |
| `rechazar()` | Rechaza una solicitud que está en proceso | Administrativo |
| `cerrar()` | Cierra definitivamente una solicitud atendida o rechazada | Administrativo |
| `cancelar()` | Cancela una solicitud pendiente | El solicitante |

---

*Este glosario es un documento vivo que debe actualizarse a medida que el dominio evoluciona.*
