# Sistema de Triage y Gestión de Solicitudes Académicas

> Proyecto desarrollado para el curso de **Programación Avanzada**  
> Programa de Ingeniería de Sistemas y Computación — Universidad del Quindío  
> Docente: Christian Andrés Candela

---

## Integrantes

| Nombre |
|--------|
| Andrés Felipe Zambrano Velasquez |
| Angelica María Reyes Barbosa |

---

## Descripción del Proyecto

El Programa de Ingeniería de Sistemas y Computación cuenta con una comunidad de más de 1.400 estudiantes, docentes y administrativos que realizan de manera permanente diversas solicitudes académicas y administrativas (homologaciones, cancelaciones, solicitudes de cupo, entre otras) a través de múltiples canales.

Este sistema resuelve la gestión ineficiente de dichas solicitudes mediante una plataforma backend 100% funcional, segura y comunicada mediante red, que permite:

- **Registrar** solicitudes de manera estructurada
- **Clasificar y priorizar** solicitudes mediante reglas de negocio claras
- **Asignar responsables** de forma controlada
- **Gestionar el ciclo de vida completo** de cada solicitud
- **Mantener un historial auditable** de todas las acciones realizadas
- **Autenticar y autorizar** usuarios mediante JWT con roles granulares
- **Consultar y filtrar** solicitudes con paginación dinámica

El sistema está diseñado siguiendo los principios de **Arquitectura Hexagonal / Domain-Driven Design (DDD)**, garantizando que las reglas del negocio vivan en el dominio y que el software represente fielmente la realidad del problema.

---

## Tecnologías

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 25 | Lenguaje principal |
| Spring Boot | 4.0.3 | Framework backend |
| Spring Security | 7.0.3 | Autenticación y autorización |
| Spring Data JPA | 4.0.3 | Persistencia |
| H2 Database | 2.4.240 | Base de datos en memoria |
| Maven | 3.x | Gestión de dependencias |
| MapStruct | 1.6.3 | Mapeo DTO ↔ Dominio ↔ JPA |
| Lombok | 1.18.42 | Reducción de boilerplate |
| Springdoc OpenAPI | 3.0.2 | Documentación Swagger UI |
| JUnit 5 | Latest | Pruebas unitarias e integración |

---

## Cómo Levantar el Proyecto

### Prerrequisitos

- Java 25 instalado
- Maven 3.x instalado

```bash
java -version   # debe mostrar 25.x.x
mvn -version    # debe mostrar 3.x.x
```

### Compilar y ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run
```

La aplicación levanta en `http://localhost:8080`

### Ejecutar pruebas

```bash
mvn test
```

---

## Acceso a Herramientas de Desarrollo

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Permite explorar y probar todos los endpoints directamente desde el navegador.

### Consola H2

```
http://localhost:8080/h2-console
```

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:solicitudesdb` |
| User Name | `sa` |
| Password | *(vacío)* |

---

## Credenciales Seed

Al iniciar la aplicación se insertan automáticamente estos usuarios de prueba:

| Email | Password | Rol | Tipo |
|-------|----------|-----|------|
| `admin@solicitudes.com` | `admin123` | ADMIN | ADMINISTRATIVO |
| `agente@solicitudes.com` | `agente123` | USER | ESTUDIANTE |

Para obtener el `codigoExterno` del admin (necesario para clasificar solicitudes), conectarse a la consola H2 y ejecutar:

```sql
SELECT codigo_externo, email, tipo_usuario FROM usuarios;
```

---

## Autenticación JWT

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin@solicitudes.com",
  "password": "admin123"
}
```

Respuesta:
```json
{
  "token": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "type": "Bearer",
  "expireAt": "2026-04-19T...",
  "roles": ["ADMIN", "ADMINISTRATIVO"]
}
```

### Uso del token en Swagger

1. Copia el valor del campo `token`
2. Haz clic en el botón **Authorize** en la parte superior de Swagger UI
3. Escribe `Bearer {token}` y confirma

### Refresh Token

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGci..."
}
```

### Logout

```http
POST /api/auth/logout
Authorization: Bearer {token}
```

---

## Guía de Inicio Rápido

### Paso 1 — Hacer login y obtener el token

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin@solicitudes.com",
  "password": "admin123"
}
```

Copia el `token` de la respuesta y autorízate en Swagger con `Bearer {token}`.

---

### Paso 2 — Crear un usuario solicitante

```http
POST /api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@uniquindio.edu.co",
  "tipoUsuario": "ESTUDIANTE"
}
```

Respuesta:
```json
{
  "id": "43483677-e069-4c57-903a-213bfdacbcba",
  "nombre": "Juan Pérez",
  "email": "juan@uniquindio.edu.co",
  "tipoUsuario": "ESTUDIANTE"
}
```

> Copia el campo `id` — es el `codigoExterno` del usuario y lo necesitarás para crear solicitudes.

---

### Paso 3 — Crear una solicitud

```http
POST /api/solicitudes
Authorization: Bearer {token}
Content-Type: application/json

{
  "descripcion": "Solicito homologación de la materia Cálculo I cursada en otra institución.",
  "solicitanteId": "43483677-e069-4c57-903a-213bfdacbcba"
}
```

Respuesta:
```json
{
  "codigo": "SOL-1776580125237",
  "descripcion": "Solicito homologación...",
  "estado": "CLASIFICACION",
  "tipoSolicitud": null,
  "prioridad": null,
  "fechaCreacion": "2026-04-19T...",
  "solicitante": { ... },
  "responsable": null
}
```

> Copia el `codigo` — lo necesitarás para los siguientes pasos.

---

### Paso 4 — Clasificar la solicitud

```http
PATCH /api/solicitudes/SOL-1776580125237/clasificar
Authorization: Bearer {token}
Content-Type: application/json

{
  "tipoSolicitud": "HOMOLOGACION",
  "adminId": "codigoExterno-del-admin"
}
```

> El `adminId` es el `codigoExterno` del admin. Consúltalo en H2 con `SELECT * FROM USUARIOS`.

La prioridad se calcula automáticamente. El estado cambia a `PENDIENTE`.

---

### Paso 5 — Asignar responsable

```http
PATCH /api/solicitudes/SOL-1776580125237/revision
Authorization: Bearer {token}
Content-Type: application/json

{
  "responsableId": "codigoExterno-del-admin"
}
```

El estado cambia a `EN_PROCESO`.

---

### Paso 6 — Atender la solicitud

```http
PATCH /api/solicitudes/SOL-1776580125237/atender
Authorization: Bearer {token}
Content-Type: application/json

{
  "adminId": "codigoExterno-del-admin"
}
```

El estado cambia a `ATENDIDA`.

---

### Paso 7 — Cerrar la solicitud

```http
PATCH /api/solicitudes/SOL-1776580125237/cerrar
Authorization: Bearer {token}
Content-Type: application/json

{
  "adminId": "codigoExterno-del-admin"
}
```

El estado cambia a `CERRADA`.

---

### Paso 8 — Ver el historial

```http
GET /api/solicitudes/SOL-1776580125237/historial
Authorization: Bearer {token}
```

---

### Paso 9 — Ver el dashboard

```http
GET /api/solicitudes/dashboard
Authorization: Bearer {token}
```

Respuesta:
```json
{
  "totalSolicitudes": 1,
  "pendientes": 0,
  "enProceso": 0,
  "atendidas": 0,
  "rechazadas": 0,
  "cerradas": 1,
  "canceladas": 0,
  "sinResponsable": 0,
  "porTipo": {
    "HOMOLOGACION": 1
  }
}
```

---

## Endpoints Disponibles

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Iniciar sesión |
| `POST` | `/api/auth/refresh` | Renovar token |
| `POST` | `/api/auth/logout` | Cerrar sesión |

### Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/usuarios` | Crear usuario |
| `GET` | `/api/usuarios` | Listar usuarios |
| `GET` | `/api/usuarios/buscar?email=` | Buscar por email |
| `GET` | `/api/usuarios/buscar?nombre=` | Buscar por nombre |
| `GET` | `/api/usuarios/{id}` | Obtener por ID |
| `PUT` | `/api/usuarios/{id}` | Actualizar usuario |
| `DELETE` | `/api/usuarios/{id}` | Eliminar usuario |
| `GET` | `/api/usuarios/{id}/solicitudes` | Solicitudes de un usuario |

### Solicitudes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/solicitudes` | Registrar solicitud |
| `GET` | `/api/solicitudes` | Listar con filtros y paginación |
| `GET` | `/api/solicitudes/dashboard` | Resumen general del sistema |
| `GET` | `/api/solicitudes/pendientes` | Solicitudes sin responsable |
| `GET` | `/api/solicitudes/vencidas?dias=7` | Solicitudes sin resolver en N días |
| `GET` | `/api/solicitudes/mis-solicitudes?solicitanteId=` | Solicitudes de un solicitante |
| `GET` | `/api/solicitudes/asignadas-a-mi?responsableId=` | Solicitudes asignadas a un responsable |
| `GET` | `/api/solicitudes/reporte/por-estado` | Reporte agrupado por estado |
| `GET` | `/api/solicitudes/reporte/por-tipo` | Reporte agrupado por tipo |
| `GET` | `/api/solicitudes/reporte/por-responsable` | Reporte por responsable |
| `GET` | `/api/solicitudes/{codigo}` | Obtener por código |
| `GET` | `/api/solicitudes/{codigo}/historial` | Historial de eventos |
| `PATCH` | `/api/solicitudes/{codigo}/clasificar` | Clasificar solicitud |
| `PATCH` | `/api/solicitudes/{codigo}/revision` | Poner en revisión |
| `PATCH` | `/api/solicitudes/{codigo}/atender` | Marcar como atendida |
| `PATCH` | `/api/solicitudes/{codigo}/rechazar` | Rechazar solicitud |
| `PATCH` | `/api/solicitudes/{codigo}/cerrar` | Cerrar solicitud |
| `PATCH` | `/api/solicitudes/{codigo}/cancelar` | Cancelar solicitud |

### Filtros disponibles en `GET /api/solicitudes`

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `estado` | enum | CLASIFICACION, PENDIENTE, EN_PROCESO, ATENDIDA, RECHAZADA, CERRADA, CANCELADA |
| `tipo` | enum | HOMOLOGACION, CANCELACION, SOLICITUD_CUPO, OTRO |
| `prioridad` | enum | ALTA, MEDIA, BAJA |
| `solicitanteId` | string | UUID del solicitante |
| `page` | int | Número de página (default: 0) |
| `size` | int | Tamaño de página (default: 10) |
| `sortBy` | string | Campo de ordenamiento (default: fechaCreacion) |

---

## Ciclo de Vida de una Solicitud

```
CLASIFICACION → PENDIENTE → EN_PROCESO → ATENDIDA  → CERRADA
                          ↘            → RECHAZADA → CERRADA
              → CANCELADA (solo el solicitante, desde PENDIENTE)
```

### Reglas de Prioridad Automática

| Tipo de Solicitud | Prioridad Base |
|-------------------|----------------|
| HOMOLOGACION | ALTA |
| SOLICITUD_CUPO | ALTA |
| CANCELACION | MEDIA |
| OTRO | BAJA |

**Envejecimiento:** Si la solicitud lleva más de 3 días sin atenderse sube un nivel. Más de 7 días sube directamente a ALTA.

---

## Códigos de Respuesta HTTP

| Código | Significado |
|--------|-------------|
| `200` | Operación exitosa |
| `201` | Recurso creado exitosamente |
| `204` | Operación exitosa sin contenido |
| `400` | Datos de entrada inválidos |
| `401` | No autenticado |
| `403` | Sin permisos para ejecutar la acción |
| `404` | Recurso no encontrado |
| `409` | Conflicto (ej. email duplicado) |
| `422` | Violación de regla de negocio del dominio |
| `500` | Error interno del servidor |

---

## Estructura del Proyecto

```
src/main/java/co/edu/uniquindio/proyecto/
├── domain/
│   ├── entity/
│   │   ├── Solicitud.java              # Agregado Raíz
│   │   └── Usuario.java               # Entidad
│   ├── valueobject/
│   │   ├── CodigoSolicitud.java
│   │   ├── Email.java
│   │   ├── EstadoSolicitud.java
│   │   ├── EventoHistorial.java
│   │   ├── Prioridad.java             # Con envejecimiento automático
│   │   ├── TipoSolicitud.java
│   │   └── TipoUsuario.java
│   ├── service/
│   │   ├── GestorSolicitudService.java
│   │   └── NotificacionService.java   # Puerto de notificaciones
│   ├── repository/
│   │   ├── SolicitudRepository.java
│   │   └── UsuarioRepository.java
│   └── exception/
│       ├── ReglaDominioException.java
│       ├── SolicitudNoEncontradaException.java
│       ├── UsuarioNoEncontradoException.java
│       └── UsuarioNoAutorizadoException.java
├── application/
│   ├── usecase/
│   │   ├── CrearSolicitudUseCase.java
│   │   ├── ClasificarSolicitudUseCase.java
│   │   ├── EnRevisionUseCase.java
│   │   ├── AtenderSolicitudUseCase.java
│   │   ├── RechazarSolicitudUseCase.java
│   │   ├── CerrarSolicitudUseCase.java
│   │   ├── CancelarSolicitudUseCase.java
│   │   ├── ConsultarSolicitudesPorEstadoUseCase.java
│   │   ├── CrearUsuarioUseCase.java
│   │   ├── ConsultarUsuariosUseCase.java
│   │   ├── ActualizarUsuarioUseCase.java
│   │   ├── EliminarUsuarioUseCase.java
│   │   └── BuscarUsuariosUseCase.java
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── service/
│       └── SecurityService.java
└── infrastructure/
    ├── config/
    │   ├── H2ConsoleConfig.java
    │   └── SwaggerConfig.java
    ├── config/setup/
    │   ├── DefaultUserInitializer.java
    │   └── DefaultUserProperties.java
    ├── notification/
    │   └── LogNotificacionService.java
    ├── persistence/jpa/
    │   ├── entity/
    │   ├── mapper/
    │   ├── SolicitudJpaDataRepository.java
    │   ├── UsuarioJpaDataRepository.java
    │   ├── SolicitudJpaRepository.java
    │   └── UsuarioJpaRepository.java
    ├── rest/
    │   ├── controllers/
    │   │   ├── SolicitudController.java
    │   │   ├── UsuarioController.java
    │   │   └── SecurityController.java
    │   ├── dto/
    │   ├── mapper/
    │   └── GlobalExceptionHandler.java
    └── security/
        ├── SecurityConfig.java
        ├── JwtConfig.java
        ├── JwtTokenProvider.java
        ├── JwtBlacklistFilter.java
        ├── TokenBlacklist.java
        ├── CustomUserDetails.java
        ├── UserConfig.java
        └── services/
            └── SecurityServiceImpl.java
```

---

## Documentación Adicional

La carpeta `/docs` contiene:

| Documento | Descripción |
|-----------|-------------|
| `glosario-lenguaje-ubicuo.md` | Definición de todos los términos clave del dominio |
| `diagrama-clases.md` | Diagrama de clases UML del modelo de dominio |
| `diagrama-estados.md` | Diagrama del ciclo de vida de una solicitud |
| `reglas-de-negocio.md` | Documentación de las 18 reglas de negocio |
| `api.yml` | Especificación OpenAPI 3.0 de todos los endpoints |

---

## Decisiones de Diseño Clave

**¿Por qué H2 en lugar de MongoDB?**  
La Entrega 02 requiere persistencia relacional con JPA/Hibernate siguiendo las guías del curso. H2 permite desarrollo y pruebas sin instalación adicional.

**¿Por qué entidades JPA separadas del dominio?**  
Para respetar la arquitectura hexagonal. Las entidades de dominio no deben conocer `@Entity`, `@Column` ni ninguna anotación de persistencia. El adaptador JPA hace la traducción.

**¿Por qué JWT con HMAC/HS256?**  
Es simétrico, sin necesidad de infraestructura de clave pública/privada, adecuado para sistemas monolíticos. El secreto se configura en `application.properties`.

**¿Por qué `TokenBlacklist` en memoria?**  
Para desarrollo y pruebas es suficiente. En producción se reemplazaría por Redis para persistencia distribuida del blacklist.

**¿Por qué `LinkedHashSet` para el historial?**  
Garantiza orden cronológico de inserción y ausencia de duplicados simultáneamente.
