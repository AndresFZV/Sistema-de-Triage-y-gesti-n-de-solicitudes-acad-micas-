package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

/**
 * Enumerador de persistencia que representa los estados del ciclo de vida
 * de una solicitud en la base de datos.
 *
 * <p>Es el espejo de {@code EstadoSolicitud} del dominio adaptado a la
 * capa de infraestructura. El mapeo entre ambos lo realiza
 * {@code SolicitudPersistenceMapper}.</p>
 */
public enum EstadoSolicitudEnum {
    CLASIFICACION,
    PENDIENTE,
    EN_PROCESO,
    ATENDIDA,
    CERRADA,
    RECHAZADA,
    CANCELADA
}