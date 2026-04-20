package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

/**
 * Enumerador de persistencia que representa la categoría de una solicitud
 * académica en la base de datos.
 *
 * <p>Es el espejo de {@code TipoSolicitud} del dominio adaptado a la
 * capa de infraestructura. El mapeo entre ambos lo realiza
 * {@code SolicitudPersistenceMapper}.</p>
 */
public enum TipoSolicitudEnum {
    /** Solicitud para homologar materias cursadas en otra institución. */
    HOMOLOGACION,
    /** Solicitud para cancelar materias del semestre. */
    CANCELACION,
    /** Solicitud para obtener un cupo en una materia. */
    SOLICITUD_CUPO,
    /** Cualquier otra solicitud que no encaje en las categorías anteriores. */
    OTRO
}