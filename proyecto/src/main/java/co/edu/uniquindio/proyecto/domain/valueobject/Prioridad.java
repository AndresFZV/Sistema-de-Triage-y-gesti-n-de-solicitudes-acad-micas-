package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.service.AsignadorPrioridadService;

/**
 * Value Object que representa el nivel de urgencia de una solicitud.
 * Es calculado automáticamente por {@link AsignadorPrioridadService}
 * con base en el tipo de solicitud y el tiempo transcurrido desde su creación.
 *
 * <p>No es asignado manualmente por ningún usuario. El sistema aplica
 * reglas de envejecimiento para evitar que solicitudes de menor prioridad
 * queden indefinidamente sin atender.</p>
 */
public enum Prioridad {

    /** Máxima urgencia. Requiere atención inmediata. */
    ALTA,

    /** Urgencia moderada. Debe atenderse en un plazo razonable. */
    MEDIA,

    /** Urgencia baja. Sin restricción de tiempo inmediata. */
    BAJA
}