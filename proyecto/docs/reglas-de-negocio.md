# Documento de Reglas de Negocio
## Sistema de Triage y Gestión de Solicitudes Académicas
### Programa de Ingeniería de Sistemas y Computación — Universidad del Quindío
### Curso: Programación Avanzada

---

## Introducción

Este documento describe las reglas de negocio identificadas durante el análisis del dominio del Sistema de Triage y Gestión de Solicitudes Académicas. Cada regla especifica qué acción regula, qué condición debe cumplirse, el RF al que está asociada y dónde vive en el código.

---

## Reglas de Negocio

### RN-01 — Cualquier usuario puede registrar una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El registro de una nueva solicitud |
| **¿Qué condición debe cumplirse?** | El solicitante no puede ser nulo |
| **¿Qué pasaría si no existiera?** | No habría control sobre quién registra solicitudes en el sistema |
| **RF asociado** | RF-01 Registro de solicitudes |
| **¿Dónde vive en el código?** | Constructor del agregado `Solicitud` |

```java
if (solicitante == null)
    throw new ReglaDominioException("El solicitante es obligatorio");
```

---

### RN-02 — Solo un administrativo puede clasificar una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La clasificación de una solicitud (asignación de tipo) |
| **¿Qué condición debe cumplirse?** | El usuario que clasifica debe tener `TipoUsuario.ADMINISTRATIVO` |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría alterar la clasificación de una solicitud, haciendo el triage inválido |
| **RF asociado** | RF-02 Clasificación de solicitudes |
| **¿Dónde vive en el código?** | Método `clasificar()` del `GestorSolicitudService` |

```java
if (!quien.esAdministrativo())
    throw new ReglaDominioException("Solo un administrativo puede clasificar una solicitud");
```

---

### RN-03 — La prioridad la asigna el sistema automáticamente según tipo y tiempo de espera

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La asignación de prioridad durante la clasificación |
| **¿Qué condición debe cumplirse?** | La prioridad se calcula con base en el `TipoSolicitud` y los días transcurridos desde la creación |
| **¿Qué pasaría si no existiera?** | Las solicitudes de menor prioridad nunca serían atendidas por saturación de prioridades altas |
| **RF asociado** | RF-03 Priorización de solicitudes |
| **¿Dónde vive en el código?** | `AsignadorPrioridadService` |

```
HOMOLOGACION, SOLICITUD_CUPO → ALTA
CANCELACION → MEDIA
OTRO → BAJA
Más de 3 días sin atender → sube un nivel
Más de 7 días sin atender → sube a ALTA directamente
```

---

### RN-04 — Solo se puede clasificar una solicitud en estado CLASIFICACION

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La clasificación de una solicitud |
| **¿Qué condición debe cumplirse?** | El estado debe ser `CLASIFICACION` |
| **¿Qué pasaría si no existiera?** | Se podría reclasificar una solicitud que ya está en proceso, alterando su flujo |
| **RF asociado** | RF-02 Clasificación / RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `clasificar()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.CLASIFICACION)
    throw new ReglaDominioException("Solo se puede clasificar una solicitud en estado CLASIFICACION");
```

---

### RN-05 — Solo un administrativo puede poner en revisión una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Poner una solicitud en revisión |
| **¿Qué condición debe cumplirse?** | El usuario que pone en revisión debe tener `TipoUsuario.ADMINISTRATIVO` |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría asignarse como responsable de una solicitud |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `enRevision()` del `GestorSolicitudService` |

```java
if (!responsable.esAdministrativo())
    throw new ReglaDominioException("Solo un administrativo puede poner en revisión una solicitud");
```

---

### RN-06 — Solo se puede poner en revisión una solicitud PENDIENTE

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Poner una solicitud en revisión |
| **¿Qué condición debe cumplirse?** | El estado debe ser `PENDIENTE` |
| **¿Qué pasaría si no existiera?** | Se podría asignar responsable a solicitudes ya cerradas o en proceso |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `enRevision()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.PENDIENTE)
    throw new ReglaDominioException("Solo se puede poner en revisión una solicitud PENDIENTE");
```

---

### RN-07 — Solo un administrativo puede rechazar una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El rechazo de una solicitud |
| **¿Qué condición debe cumplirse?** | El usuario que rechaza debe tener `TipoUsuario.ADMINISTRATIVO` |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría rechazar solicitudes de otros |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `rechazar()` del `GestorSolicitudService` |

```java
if (!quien.esAdministrativo())
    throw new ReglaDominioException("Solo un administrativo puede rechazar una solicitud");
```

---

### RN-08 — Solo se puede rechazar una solicitud EN_PROCESO

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El rechazo de una solicitud |
| **¿Qué condición debe cumplirse?** | El estado debe ser `EN_PROCESO` |
| **¿Qué pasaría si no existiera?** | Se podrían rechazar solicitudes que aún no han sido revisadas |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `rechazar()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.EN_PROCESO)
    throw new ReglaDominioException("Solo se puede rechazar una solicitud EN_PROCESO");
```

---

### RN-09 — Solo un administrativo puede marcar una solicitud como atendida

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Marcar una solicitud como atendida |
| **¿Qué condición debe cumplirse?** | El usuario debe tener `TipoUsuario.ADMINISTRATIVO` |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría marcar solicitudes como atendidas sin haberlas procesado |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `atendida()` del `GestorSolicitudService` |

```java
if (!quien.esAdministrativo())
    throw new ReglaDominioException("Solo un administrativo puede marcar una solicitud como atendida");
```

---

### RN-10 — Solo se puede atender una solicitud EN_PROCESO

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Marcar una solicitud como atendida |
| **¿Qué condición debe cumplirse?** | El estado debe ser `EN_PROCESO` |
| **¿Qué pasaría si no existiera?** | Se podrían marcar como atendidas solicitudes que no han sido revisadas |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `atendida()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.EN_PROCESO)
    throw new ReglaDominioException("Solo se puede atender una solicitud EN_PROCESO");
```

---

### RN-11 — Solo un administrativo puede cerrar una solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El cierre de una solicitud |
| **¿Qué condición debe cumplirse?** | El usuario que cierra debe tener `TipoUsuario.ADMINISTRATIVO` |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría cerrar solicitudes sin autorización |
| **RF asociado** | RF-08 Cierre de solicitudes |
| **¿Dónde vive en el código?** | Método `cerrar()` del `GestorSolicitudService` |

```java
if (!quien.esAdministrativo())
    throw new ReglaDominioException("Solo un administrativo puede cerrar una solicitud");
```

---

### RN-12 — Solo se puede cerrar una solicitud ATENDIDA o RECHAZADA

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El cierre de una solicitud |
| **¿Qué condición debe cumplirse?** | El estado debe ser `ATENDIDA` o `RECHAZADA` |
| **¿Qué pasaría si no existiera?** | Se podrían cerrar solicitudes que aún están en proceso |
| **RF asociado** | RF-08 Cierre de solicitudes / RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `cerrar()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.ATENDIDA && this.estado != EstadoSolicitud.RECHAZADA)
    throw new ReglaDominioException("Solo se puede cerrar una solicitud ATENDIDA o RECHAZADA");
```

---

### RN-13 — Solo el solicitante puede cancelar su propia solicitud

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La cancelación de una solicitud |
| **¿Qué condición debe cumplirse?** | El usuario que cancela debe ser el mismo que registró la solicitud |
| **¿Qué pasaría si no existiera?** | Cualquier usuario podría cancelar solicitudes ajenas |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `cancelar()` del agregado `Solicitud` |

```java
if (!quien.getId().equals(this.solicitante.getId()))
    throw new ReglaDominioException("Solo el solicitante puede cancelar su propia solicitud");
```

---

### RN-14 — Solo se puede cancelar una solicitud PENDIENTE

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | La cancelación de una solicitud |
| **¿Qué condición debe cumplirse?** | El estado debe ser `PENDIENTE` |
| **¿Qué pasaría si no existiera?** | Se podrían cancelar solicitudes que ya están siendo atendidas |
| **RF asociado** | RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Método `cancelar()` del agregado `Solicitud` |

```java
if (this.estado != EstadoSolicitud.PENDIENTE)
    throw new ReglaDominioException("Solo se puede cancelar una solicitud PENDIENTE");
```

---

### RN-15 — El historial no puede modificarse directamente

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | El acceso y modificación del historial de una solicitud |
| **¿Qué condición debe cumplirse?** | El historial solo puede ser modificado a través de los métodos de negocio del agregado `Solicitud` |
| **¿Qué pasaría si no existiera?** | Cualquier clase externa podría agregar o eliminar eventos del historial, rompiendo la auditoría |
| **RF asociado** | RF-06 Historial auditable |
| **¿Dónde vive en el código?** | El campo `historial` es `private final` con `LinkedHashSet`. `registrarEvento()` es `private`. `getHistorial()` retorna `Collections.unmodifiableList()` |

```java
public List<EventoHistorial> getHistorial() {
    return Collections.unmodifiableList(new ArrayList<>(historial));
}

private void registrarEvento(EventoHistorial evento) {
    this.historial.add(evento);
}
```

---

### RN-16 — El estado y el historial deben estar siempre sincronizados

| Campo | Descripción |
|-------|-------------|
| **¿Qué acción regula?** | Cualquier cambio de estado de la solicitud |
| **¿Qué condición debe cumplirse?** | Cada vez que cambia el estado, debe registrarse un evento en el historial en la misma operación |
| **¿Qué pasaría si no existiera?** | El historial no reflejaría la realidad del trámite, haciendo la auditoría inútil |
| **RF asociado** | RF-06 Historial auditable / RF-04 Gestión del ciclo de vida |
| **¿Dónde vive en el código?** | Cada método de negocio del agregado `Solicitud` llama a `registrarEvento()` tras cambiar el estado |

---

### RN-17 — El email de un usuario debe tener formato válido

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

### RN-18 — Una solicitud debe tener descripción al registrarse

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
| RN-02 | RF-02 | `GestorSolicitudService.clasificar()` |
| RN-03 | RF-03 | `AsignadorPrioridadService` |
| RN-04 | RF-02, RF-04 | `Solicitud.clasificar()` |
| RN-05 | RF-04 | `GestorSolicitudService.enRevision()` |
| RN-06 | RF-04 | `Solicitud.enRevision()` |
| RN-07 | RF-04 | `GestorSolicitudService.rechazar()` |
| RN-08 | RF-04 | `Solicitud.rechazar()` |
| RN-09 | RF-04 | `GestorSolicitudService.atendida()` |
| RN-10 | RF-04 | `Solicitud.atendida()` |
| RN-11 | RF-08 | `GestorSolicitudService.cerrar()` |
| RN-12 | RF-08, RF-04 | `Solicitud.cerrar()` |
| RN-13 | RF-04 | `Solicitud.cancelar()` |
| RN-14 | RF-04 | `Solicitud.cancelar()` |
| RN-15 | RF-06 | `Solicitud.getHistorial()`, `Solicitud.registrarEvento()` |
| RN-16 | RF-06, RF-04 | Todos los métodos de negocio de `Solicitud` |
| RN-17 | RF-13 | `Email` constructor |
| RN-18 | RF-01 | `Solicitud` constructor |