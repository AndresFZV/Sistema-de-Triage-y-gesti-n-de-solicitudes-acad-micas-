# Sistema de Triage y Gestión de Solicitudes Académicas

> Proyecto desarrollado para el curso de **Programación Avanzada**  
> Programa de Ingeniería de Sistemas y Computación — Universidad del Quindío  
> Docente: Christian Andrés Candela

---

## Integrantes

| Nombre | Código |
|--------|--------|
| Andrés Felipe Zambrano Velasquez |
| Angelica María Reyes Barbosa |

---

## Descripción del Proyecto

El Programa de Ingeniería de Sistemas y Computación cuenta con una comunidad de más de 1.400 estudiantes, docentes y administrativos que realizan de manera permanente diversas solicitudes académicas y administrativas (homologaciones, cancelaciones, solicitudes de cupo, entre otras) a través de múltiples canales.

Este sistema busca resolver la gestión ineficiente de dichas solicitudes mediante una plataforma unificada que permite:

- **Registrar** solicitudes de manera estructurada
- **Clasificar y priorizar** solicitudes mediante reglas de negocio claras
- **Asignar responsables** de forma controlada
- **Gestionar el ciclo de vida completo** de cada solicitud
- **Mantener un historial auditable** de todas las acciones realizadas

El sistema está diseñado siguiendo los principios de **Domain-Driven Design (DDD)**, garantizando que las reglas del negocio vivan en el dominio y que el software represente fielmente la realidad del problema.

---

## Tecnologías

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 25 | Lenguaje principal |
| Spring Boot | 4.0.3 | Framework backend |
| Maven | 3.x | Gestión de dependencias |
| MongoDB | 7.x | Base de datos |
| Lombok | Latest | Reducción de boilerplate |
| JUnit 5 | Latest | Pruebas unitarias |

---

## Estructura del Proyecto

```
src
├── main
│   └── java
│       └── co.edu.uniquindio.proyecto
│           ├── domain
│           │   ├── entity
│           │   │   ├── Solicitud.java          # Agregado Raíz
│           │   │   └── Usuario.java            # Entidad
│           │   ├── valueobject
│           │   │   ├── CodigoSolicitud.java
│           │   │   ├── Email.java
│           │   │   ├── EstadoSolicitud.java
│           │   │   ├── EventoHistorial.java
│           │   │   ├── Prioridad.java
│           │   │   ├── TipoNotificacion.java
│           │   │   ├── TipoSolicitud.java
│           │   │   └── TipoUsuario.java
│           │   ├── service
│           │   │   ├── GestorSolicitudService.java      # Reglas de roles
│           │   │   ├── AsignadorPrioridadService.java   # Prioridad automática
│           │   │   └── NotificadorSolicitudes.java
│           │   └── exception
│           │       └── ReglaDominioException.java
│           ├── application              # Casos de uso (próximas entregas)
│           └── infrastructure          # Persistencia y REST (próximas entregas)
└── test
    └── java
        └── co.edu.uniquindio.proyecto
            └── domain
                ├── entity
                │   ├── SolicitudTest.java
                │   └── UsuarioTest.java
                ├── valueobject
                │   ├── EmailTest.java
                │   └── CodigoSolicitudTest.java
                └── service
                    ├── GestorSolicitudServiceTest.java
                    ├── AsignadorPrioridadServiceTest.java
                    └── NotificadorSolicitudesTest.java
```

---

## Documentación

La carpeta `/docs` contiene los siguientes artefactos de análisis y diseño:

| Documento | Descripción |
|-----------|-------------|
| `glosario-lenguaje-ubicuo.md` | Definición de todos los términos clave del dominio |
| `Diagrama de clases.jpg` | Diagrama de clases UML del modelo de dominio |
| `Diagrama de estados.jpg` | Diagrama del ciclo de vida de una solicitud |
| `reglas-de-negocio.md` | Documentación completa de las 18 reglas de negocio identificadas |

---

## Cómo Compilar y Ejecutar las Pruebas

### Prerrequisitos

- Java 25 instalado
- Maven 3.x instalado
- Verificar instalaciones:

```bash
java -version
mvn -version
```

### Compilar el proyecto

```bash
mvn clean compile
```

### Ejecutar las pruebas unitarias

```bash
mvn test
```

### Ver el reporte de pruebas

Después de ejecutar `mvn test`, el reporte se genera en:

```
target/surefire-reports/
```

### Resultado esperado

```
[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Cobertura de Requisitos Funcionales

| RF | Descripción | Estado |
|----|-------------|--------|
| RF-01 | Registro de solicitudes | ✅ Implementado |
| RF-02 | Clasificación de solicitudes | ✅ Implementado |
| RF-03 | Priorización de solicitudes | ✅ Implementado |
| RF-04 | Gestión del ciclo de vida | ✅ Implementado |
| RF-06 | Historial auditable | ✅ Implementado |
| RF-08 | Cierre de solicitudes | ✅ Implementado |
| RF-11 | Independencia de IA en el diseño | ✅ Diseñado |
| RF-13 | Definición de roles | ✅ Implementado |

---

## Decisiones de Diseño

**¿Por qué MongoDB en lugar de H2?**  
MongoDB es una base de datos documental que se adapta naturalmente al modelo de agregados de DDD. Un documento de solicitud puede contener su historial embebido, respetando los límites del agregado sin necesidad de joins.

**¿Por qué `record` para los Value Objects?**  
Los `record` de Java garantizan inmutabilidad, generan automáticamente `equals()`, `hashCode()` y `toString()` por valor, y permiten validación en el constructor compacto. Esto los hace ideales para Value Objects.

**¿Por qué los métodos de negocio viven dentro de las entidades?**  
Siguiendo el principio "Tell, don't ask" y evitando el antipatrón de Modelo Anémico. Las reglas del negocio viven junto a los datos que gobiernan, haciendo el sistema más expresivo, cohesivo y fácil de mantener.

**¿Por qué las reglas de roles están en `GestorSolicitudService` y no en `Solicitud`?**  
`Solicitud` no debería saber nada sobre roles de usuarios. Su responsabilidad es gestionar su propio ciclo de vida. Las reglas de quién puede hacer qué son reglas del proceso, no invariantes del agregado. Por eso viven en un Servicio de Dominio separado.

**¿Por qué la prioridad la asigna el sistema automáticamente?**  
Para evitar sesgos humanos y garantizar consistencia. El `AsignadorPrioridadService` aplica reglas objetivas basadas en el tipo de solicitud y el tiempo de espera, incluyendo envejecimiento de prioridad para evitar que solicitudes de baja prioridad queden indefinidamente sin atender.

**¿Por qué `LinkedHashSet` para el historial?**  
Porque el historial necesita dos garantías simultáneas: orden cronológico de inserción y ausencia de duplicados. `LinkedHashSet` es la única colección de Java que ofrece ambas a la vez.
