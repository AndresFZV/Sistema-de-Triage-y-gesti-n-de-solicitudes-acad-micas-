package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

/**
 * Enumerador de persistencia que representa el rol de un usuario
 * dentro del dominio académico.
 *
 * <p>Es el espejo de {@code TipoUsuario} del dominio adaptado a la
 * capa de infraestructura. Es independiente de {@link RolSeguridadEnum},
 * que pertenece exclusivamente a Spring Security.</p>
 */
public enum TipoUsuarioEnum {
    /** Persona que registra y puede cancelar sus propias solicitudes. */
    ESTUDIANTE,
    /** Docente del programa académico. Puede registrar solicitudes. */
    DOCENTE,
    /** Personal administrativo. Gestiona el ciclo de vida completo de las solicitudes. */
    ADMINISTRATIVO
}