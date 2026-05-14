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

Este sistema resuelve la gestión ineficiente de dichas solicitudes mediante una plataforma fullstack funcional, segura y con integración de inteligencia artificial, que permite:

- **Registrar** solicitudes de manera estructurada
- **Clasificar y priorizar** solicitudes mediante reglas de negocio claras y asistencia de IA
- **Asignar responsables** de forma controlada con selección desde un listado de administradores
- **Gestionar el ciclo de vida completo** de cada solicitud
- **Mantener un historial auditable** de todas las acciones realizadas
- **Autenticar y autorizar** usuarios mediante JWT con roles granulares
- **Consultar y filtrar** solicitudes con paginación, búsqueda y filtros dinámicos
- **Resumir y analizar** solicitudes con inteligencia artificial (Groq - LLaMA 3.3)

El sistema está diseñado siguiendo los principios de **Arquitectura Hexagonal / Domain-Driven Design (DDD)**, garantizando que las reglas del negocio vivan en el dominio y que el software represente fielmente la realidad del problema.

---

## Tecnologías

### Backend

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
| Groq API | — | Inteligencia artificial (LLaMA 3.3 70B) |

### Frontend

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Angular | 21 | Framework frontend |
| TypeScript | 5.x | Lenguaje principal |
| PrimeNG | 21.1.6 | Componentes UI |
| Bootstrap | 5.3.6 | Layout y estilos base |
| Chart.js | — | Gráficas del dashboard |
| Font Awesome | 6.x | Iconografía |
| PrimeIcons | — | Iconografía PrimeNG |

---

## Cómo Levantar el Proyecto

### Prerrequisitos

- Java 25 instalado
- Maven 3.x instalado
- Node.js 20+ instalado
- Angular CLI 21 instalado

```bash
java -version     # debe mostrar 25.x.x
mvn -version      # debe mostrar 3.x.x
node -version     # debe mostrar 20.x.x
ng version        # debe mostrar 21.x.x
```

### Backend

```bash
cd proyecto

# Configurar la API key de Groq en application.properties
# groq.api.key=gsk_TU_API_KEY_AQUI

# Compilar y ejecutar
mvn clean compile
mvn spring-boot:run
```

La aplicación levanta en `http://localhost:8080`

### Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm start
```

La aplicación levanta en `http://localhost:4200`

### Ejecutar pruebas (backend)

```bash
mvn test
```

---

## Configuración de Inteligencia Artificial (RF-11)

El sistema integra IA mediante la API de **Groq** con el modelo **LLaMA 3.3 70B Versatile**. La IA es un componente opcional — el sistema funciona completamente sin ella (RF-11: robustez y autonomía).

### Obtener API Key de Groq

1. Crear cuenta en `https://console.groq.com`
2. Ir a **API Keys** → **Create API Key**
3. Copiar la key (empieza con `gsk_...`)

### Configurar en el backend

En `src/main/resources/application.properties`:

```properties
groq.api.key=gsk_TU_API_KEY_AQUI
groq.api.url=https://api.groq.com/openai/v1/chat/completions
groq.model=llama-3.3-70b-versatile
```

> ⚠️ **No commitear la API key**. Agregar al `.gitignore` o usar variables de entorno.

### Funcionalidades de IA

| Endpoint | Descripción | Fallback si falla |
|----------|-------------|-------------------|
| `POST /api/ia/sugerir-tipo` | Sugiere el tipo de solicitud basado en la descripción | Retorna `OTRO` |
| `POST /api/ia/resumir` | Genera un resumen ejecutivo de la descripción | Retorna descripción original |
| `POST /api/ia/validar-descripcion` | Valida si la descripción es clara y suficiente | Retorna `OK` |

---

## Acceso a Herramientas de Desarrollo

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

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
  "expireAt": "2026-...",
  "roles": ["ADMIN", "ADMINISTRATIVO"]
}
```

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

## Guía de Inicio Rápido (Backend)

### Paso 1 — Login

```http
POST /api/auth/login
{ "username": "admin@solicitudes.com", "password": "admin123" }
```

### Paso 2 — Crear solicitud

```http
POST /api/solicitudes
Authorization: Bearer {token}

{
  "descripcion": "Solicito homologación de Cálculo I.",
  "solicitanteId": "uuid-del-solicitante"
}
```

### Paso 3 — Clasificar

```http
PATCH /api/solicitudes/{codigo}/clasificar
{ "tipoSolicitud": "HOMOLOGACION", "adminId": "uuid-del-admin" }
```

### Paso 4 — Poner en revisión

```http
PATCH /api/solicitudes/{codigo}/revision
{ "responsableId": "uuid-del-responsable" }
```

### Paso 5 — Atender

```http
PATCH /api/solicitudes/{codigo}/atender
{ "adminId": "uuid-del-admin" }
```

### Paso 6 — Cerrar

```http
PATCH /api/solicitudes/{codigo}/cerrar
{ "adminId": "uuid-del-admin" }
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
| `POST` | `/api/usuarios` | Crear usuario (público) |
| `GET` | `/api/usuarios` | Listar usuarios |
| `GET` | `/api/usuarios/buscar?email=` | Buscar por email |
| `GET` | `/api/usuarios/{id}` | Obtener por ID |
| `PUT` | `/api/usuarios/{id}` | Actualizar usuario |
| `DELETE` | `/api/usuarios/{id}` | Eliminar usuario |
| `GET` | `/api/usuarios/{id}/solicitudes` | Solicitudes de un usuario |

### Solicitudes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/solicitudes` | Registrar solicitud |
| `GET` | `/api/solicitudes` | Listar con filtros y paginación |
| `GET` | `/api/solicitudes/dashboard` | Resumen general |
| `GET` | `/api/solicitudes/{codigo}` | Obtener por código |
| `GET` | `/api/solicitudes/{codigo}/historial` | Historial de eventos |
| `PATCH` | `/api/solicitudes/{codigo}/clasificar` | Clasificar solicitud |
| `PATCH` | `/api/solicitudes/{codigo}/revision` | Poner en revisión |
| `PATCH` | `/api/solicitudes/{codigo}/atender` | Marcar como atendida |
| `PATCH` | `/api/solicitudes/{codigo}/rechazar` | Rechazar solicitud |
| `PATCH` | `/api/solicitudes/{codigo}/cerrar` | Cerrar solicitud |
| `PATCH` | `/api/solicitudes/{codigo}/cancelar` | Cancelar solicitud |

### Inteligencia Artificial

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/ia/sugerir-tipo` | Sugerir tipo de solicitud |
| `POST` | `/api/ia/resumir` | Resumir descripción |
| `POST` | `/api/ia/validar-descripcion` | Validar claridad de descripción |

---

## Ciclo de Vida de una Solicitud

```
CLASIFICACION → PENDIENTE → EN_PROCESO → ATENDIDA  → CERRADA
                                       → RECHAZADA → CERRADA
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

## Funcionalidades del Frontend

### Roles y vistas

| Rol | Vistas disponibles |
|-----|--------------------|
| ADMIN / ADMINISTRATIVO | Dashboard, Todas las solicitudes, Gestión de usuarios, Perfil |
| ESTUDIANTE / DOCENTE | Mis solicitudes, Nueva solicitud, Perfil |

### Características principales

- **Login y registro** con componentes PrimeNG y validación reactiva
- **Guards de ruta** por autenticación y rol
- **Interceptor JWT** con refresh token automático
- **Dashboard** con gráficas de torta (por estado) y barras (por tipo)
- **Lista de solicitudes** con filtros por estado/tipo/prioridad, buscador y paginación
- **Detalle de solicitud** con historial, acciones por rol y resumen IA
- **Nueva solicitud** con análisis IA — sugerencia de tipo y validación de descripción
- **Perfil de usuario** — ver y editar datos propios
- **Gestión de usuarios** con confirmación `p-dialog` antes de eliminar
- **Toasts** de notificación con PrimeNG
- **Diseño institucional** con paleta de colores UniQuindío (verde y hueso)

---

## Estructura del Proyecto

```
proyecto/                          # Backend Spring Boot
├── src/main/java/.../
│   ├── domain/                    # Núcleo del negocio (DDD)
│   │   ├── entity/
│   │   ├── valueobject/
│   │   ├── service/
│   │   ├── repository/
│   │   └── exception/
│   ├── application/               # Casos de uso
│   │   └── usecase/
│   └── infrastructure/            # Adaptadores
│       ├── ia/                    # Integración Groq IA
│       ├── persistence/jpa/
│       ├── rest/controllers/
│       └── security/
└── src/main/resources/
    └── application.properties

frontend/                          # Frontend Angular 21
├── src/app/
│   ├── componentes/
│   │   ├── login/
│   │   ├── registro/
│   │   ├── dashboard/
│   │   ├── lista-solicitudes/
│   │   ├── solicitud-detalle/
│   │   ├── nueva-solicitud/
│   │   ├── usuarios/
│   │   ├── perfil/
│   │   ├── header/
│   │   ├── footer/
│   │   └── unauthorized/
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   ├── public-guard.ts
│   │   └── roles-guard.ts
│   ├── interceptores/
│   │   └── auth.interceptor.ts
│   ├── servicios/
│   │   ├── auth.service.ts
│   │   ├── solicitudes.service.ts
│   │   └── notificacion.service.ts
│   └── modelos/
└── angular.json
```

---

## Decisiones de Diseño Clave

**¿Por qué H2 en lugar de PostgreSQL?**  
H2 permite desarrollo y pruebas sin instalación adicional. En producción se migraría a PostgreSQL.

**¿Por qué entidades JPA separadas del dominio?**  
Para respetar la arquitectura hexagonal. Las entidades de dominio no conocen `@Entity` ni anotaciones de persistencia.

**¿Por qué JWT con HMAC/HS256?**  
Es simétrico, sin necesidad de infraestructura de clave pública/privada, adecuado para sistemas monolíticos.

**¿Por qué la IA es opcional (RF-11)?**  
El sistema debe ser autónomo. Si Groq falla o no hay conexión, todas las funcionalidades principales siguen operando. La IA es un plus que mejora la experiencia pero no es un componente crítico.

**¿Por qué Groq en lugar de OpenAI?**  
Groq es gratuito con límites generosos y ofrece el modelo LLaMA 3.3 70B que es suficientemente potente para las tareas del sistema.

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
