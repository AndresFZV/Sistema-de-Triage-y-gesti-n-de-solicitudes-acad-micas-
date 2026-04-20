package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

/**
 * Enumerador de roles de seguridad para Spring Security.
 *
 * <p>Es independiente de {@link TipoUsuarioEnum}, que pertenece al dominio
 * de negocio. Este enumerador es exclusivo de la capa de seguridad e
 * infraestructura y determina qué endpoints puede acceder cada usuario.</p>
 */
public enum RolSeguridadEnum {
    /** Acceso completo al sistema. Puede gestionar el ciclo de vida de solicitudes. */
    ADMIN,
    /** Acceso limitado. Puede registrar y consultar sus propias solicitudes. */
    USER
}