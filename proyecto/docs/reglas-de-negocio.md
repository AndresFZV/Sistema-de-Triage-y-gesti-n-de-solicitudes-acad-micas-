# Documento de Reglas de Negocio
## Sistema de Triage y Gestión de Solicitudes Académicas
### Programa de Ingeniería de Sistemas y Computación — Universidad del Quindío
### Curso: Programación Avanzada

---

## Introducción

Este documento describe las reglas de negocio identificadas durante el análisis del dominio del Sistema de Triage y Gestión de Solicitudes Académicas. Cada regla especifica qué acción regula, qué condición debe cumplirse, el RF al que está asociada y dónde vive en el código.

---

## Reglas de Negocio

### RN-01 — Solo un estudiante puede registrar una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El registro de una nueva solicitud |
| **¿Qué condición debe cumplirse?** | El solicitante debe tener `TipoUsuario.ESTUDIANTE` |
| **¿Qué pasaría si no existiera?** | Docentes o administrativos podrían registrar solicitudes en nombre de estudiantes, rompiendo la trazabilidad del trámite |
| **RF asociado** | RF-01 Registro de solicitudes |
| **¿Dónde vive en el código?** | Constructor de la entidad `Solicitud` |

```java
if (!solicitante.esEstudiante())
    throw new ReglaDominioException("Solo un estudiante puede registrar una solicitud");
```

---

### RN-02 — Solo un administrativo puede clasificar una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La clasificación de una solicitud (asignación de tipo, prioridad y responsable) |
| **¿Qué condición debe cumplirse?** | El usuario que clasifica debe tener `TipoUsuario.ADMINISTRATIVO` |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría alterar la prioridad y clasificación de una solicitud, haciendo el triage inválido |
| **RF asociado** | RF-02 Clasificación / RF-03 Priorización |
| **¿Dónde vive en el código?** | Método `clasificar()` del agregado `Solicitud` |

```java
if (!responsable.esAdministrativo())
    throw new ReglaDominioException("Solo un administrativo puede clasificar una solicitud");
```

---

### RN-03 — Una solicitud cerrada no puede modificarse

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Cualquier acción que intente modificar el estado de una solicitud cerrada o rechazada |
| **¿Qué condición debe cumplirse?** | El estado de la solicitud no debe ser `CERRADA` ni `RECHAZADA` |
| **¿Qué pasaría si no existiera?** | Se podrían alterar trámites ya finalizados, rompiendo la integridad del historial |
| **RF asociado** | RF-08 Cierre de solicitudes / RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método privado `estaCerrada()` del agregado `Solicitud`, invocado en `clasificar()`, `rechazar()` y `cerrar()` |

```java
private boolean estaCerrada() {
    return this.estado == EstadoSolicitud.CERRADA
            || this.estado == EstadoSolicitud.RECHAZADA;
}
```

---

### RN-04 — No se puede cerrar una solicitud sin responsable asignado

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El cierre de una solicitud |
| **¿Qué condición debe cumplirse?** | La solicitud debe tener un responsable asignado (`responsable != null`) |
| **¿Qué pasaría si no existiera?** | Solicitudes quedarían cerradas sin que nadie las haya atendido, sin trazabilidad del responsable |
| **RF asociado** | RF-08 Cierre de solicitudes |
| **¿Dónde vive en el código?** | Método `cerrar()` del agregado `Solicitud` |

```java
if (this.responsable == null)
    throw new ReglaDominioException("No se puede cerrar una solicitud sin responsable asignado");
```

---

### RN-05 — Solo se puede cerrar una solicitud que haya sido atendida

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El cierre de una solicitud |
| **¿Qué condición debe cumplirse?** | El estado de la solicitud debe ser `ATENDIDA` |
| **¿Qué pasaría si no existiera?** | Se podrían cerrar solicitudes que aún están en proceso, saltándose etapas obligatorias del ciclo de vida |
| **RF asociado** | RF-08 Cierre de solicitudes / RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `cerrar()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.ATENDIDA)
    throw new ReglaDominioException("No se puede cerrar una solicitud que no ha sido atendida");
```

---

### RN-06 — Solo se puede atender una solicitud que esté en proceso

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Marcar una solicitud como atendida |
| **¿Qué condición debe cumplirse?** | El estado de la solicitud debe ser `EN_PROCESO` |
| **¿Qué pasaría si no existiera?** | Se podrían marcar como atendidas solicitudes que ni siquiera han sido clasificadas |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `marcarComoAtendida()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.EN_PROCESO)
    throw new ReglaDominioException("Solo se puede atender una solicitud que esté en proceso");
```

---

### RN-07 — El tipo de solicitud es obligatorio al clasificar

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La clasificación de una solicitud |
| **¿Qué condición debe cumplirse?** | El `TipoSolicitud` no puede ser nulo |
| **¿Qué pasaría si no existiera?** | Existirían solicitudes clasificadas sin tipo, haciendo imposible el triage |
| **RF asociado** | RF-02 Clasificación de solicitudes |
| **¿Dónde vive en el código?** | Método `clasificar()` del agregado `Solicitud` |

```java
if (tipo == null)
    throw new ReglaDominioException("El tipo de solicitud es obligatorio");
```

---

### RN-08 — La prioridad es obligatoria al clasificar

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La clasificación de una solicitud |
| **¿Qué condición debe cumplirse?** | La `Prioridad` no puede ser nula |
| **¿Qué pasaría si no existiera?** | Existirían solicitudes sin prioridad asignada, impidiendo ordenar su atención por urgencia |
| **RF asociado** | RF-03 Priorización de solicitudes |
| **¿Dónde vive en el código?** | Método `clasificar()` del agregado `Solicitud` |

```java
if (prioridad == null)
    throw new ReglaDominioException("La prioridad es obligatoria");
```

---

### RN-09 — El historial no puede modificarse directamente

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El acceso y modificación del historial de una solicitud |
| **¿Qué condición debe cumplirse?** | El historial solo puede ser modificado a través de los métodos de negocio del agregado `Solicitud` |
| **¿Qué pasaría si no existiera?** | Cualquier clase externa podría agregar o eliminar eventos del historial, rompiendo la auditoría del trámite |
| **RF asociado** | RF-06 Historial auditable |
| **¿Dónde vive en el código?** | El campo `historial` es `private final` en `Solicitud`. El método `registrarEvento()` es `private`. `getHistorial()` retorna `Collections.unmodifiableList()` |

```java
public List<EventoHistorial> getHistorial() {
    return Collections.unmodifiableList(historial);
}

private void registrarEvento(EventoHistorial evento) {
    this.historial.add(evento);
}
```

---

### RN-10 — El estado y el historial deben estar siempre sincronizados

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Cualquier cambio de estado de la solicitud |
| **¿Qué condición debe cumplirse?** | Cada vez que cambia el estado, debe registrarse un evento en el historial en la misma operación |
| **¿Qué pasaría si no existiera?** | El historial no reflejaría la realidad del trámite, haciendo la auditoría inútil |
| **RF asociado** | RF-06 Historial auditable / RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Cada método de negocio del agregado `Solicitud` llama a `registrarEvento()` justo después de cambiar el estado |

---

### RN-11 — El email de un usuario debe tener formato válido

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La creación de un usuario |
| **¿Qué condición debe cumplirse?** | El email debe cumplir el patrón `usuario@dominio.extension` |
| **¿Qué pasaría si no existiera?** | Podrían registrarse usuarios con emails inválidos, imposibilitando las notificaciones |
| **RF asociado** | RF-13 Definición de roles |
| **¿Dónde vive en el código?** | Constructor del Value Object `Email` |

```java
if (!valor.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
    throw new ReglaDominioException("El email no tiene un formato válido");
```

---

### RN-12 — Una solicitud debe tener descripción al registrarse

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El registro de una nueva solicitud |
| **¿Qué condición debe cumplirse?** | La descripción no puede ser nula ni estar vacía |
| **¿Qué pasaría si no existiera?** | Podrían existir solicitudes sin contexto, haciendo imposible su atención |
| **RF asociado** | RF-01 Registro de solicitudes |
| **¿Dónde vive en el código?** | Constructor del agregado `Solicitud` |

```java
if (descripcion == null || descripcion.isBlank())
    throw new ReglaDominioException("La descripción no puede estar vacía");
```

---

## Resumen de Trazabilidad

| Regla | RF Asociado | Ubicación en el código |
|-------|-------------|----------------------|
| RN-01 | RF-01 | `Solicitud` constructor |
| RN-02 | RF-02, RF-03 | `Solicitud.clasificar()` |
| RN-03 | RF-08, RF-04 | `Solicitud.estaCerrada()` |
| RN-04 | RF-08 | `Solicitud.cerrar()` |
| RN-05 | RF-08, RF-04 | `Solicitud.cerrar()` |
| RN-06 | RF-04 | `Solicitud.marcarComoAtendida()` |
| RN-07 | RF-02 | `Solicitud.clasificar()` |
| RN-08 | RF-03 | `Solicitud.clasificar()` |
| RN-09 | RF-06 | `Solicitud.getHistorial()`, `Solicitud.registrarEvento()` |
| RN-10 | RF-06, RF-04 | Todos los métodos de negocio de `Solicitud` |
| RN-11 | RF-13 | `Email` constructor |
| RN-12 | RF-01 | `Solicitud` constructor |