package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.service.AsignadorPrioridadService;

/**
 * Value Object que clasifica el propósito de una solicitud académica.
 * Es asignado por un administrativo durante la clasificación y determina
 * la prioridad base calculada por {@link AsignadorPrioridadService}.
 *
 * <p>Es un conjunto cerrado de valores inmutables.</p>
 */
public enum TipoSolicitud {

    /** Solicitud para homologar materias cursadas en otra institución. Alta prioridad base. */
    HOMOLOGACION,

    /** Solicitud para cancelar una o varias materias del semestre. Prioridad media base. */
    CANCELACION,

    /** Solicitud para obtener un cupo en una materia. Alta prioridad base. */
    SOLICITUD_CUPO,

    /** Cualquier otra solicitud que no encaje en las categorías anteriores. Baja prioridad base. */
    OTRO
}