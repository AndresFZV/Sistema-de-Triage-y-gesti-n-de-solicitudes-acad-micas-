package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

/**
 * Enumerador de persistencia que representa el nivel de prioridad
 * de una solicitud en la base de datos.
 *
 * <p>Es el espejo de {@code Prioridad} del dominio adaptado a la
 * capa de infraestructura. El mapeo entre ambos lo realiza
 * {@code SolicitudPersistenceMapper}.</p>
 */
public enum PrioridadEnum {
    ALTA,
    MEDIA,
    BAJA
}