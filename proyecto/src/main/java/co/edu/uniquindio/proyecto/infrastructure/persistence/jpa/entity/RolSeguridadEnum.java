package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

/**
 * Enumerador de roles de seguridad para Spring Security.
 * Es independiente de TipoUsuarioEnum (que pertenece al dominio).
 */
public enum RolSeguridadEnum {
    ADMIN,
    USER
}