package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;

/**
 * Value Object que representa el estado actual de una solicitud
 * dentro de su ciclo de vida.
 *
 * <p>Las transiciones válidas entre estados son controladas exclusivamente
 * por el agregado raíz {@link Solicitud}. Ninguna clase externa puede
 * cambiar el estado directamente.</p>
 *
 * <p>Flujo principal:</p>
 * <pre>
 * CLASIFICACION → PENDIENTE → EN_PROCESO → ATENDIDA → CERRADA
 *                     ↓              ↓
 *                 CANCELADA      RECHAZADA → CERRADA
 * </pre>
 */
public enum EstadoSolicitud {

    /** Estado inicial. La solicitud ha sido registrada y espera ser clasificada. */
    CLASIFICACION,

    /** La solicitud ha sido clasificada y espera ser asignada a un responsable. */
    PENDIENTE,

    /** La solicitud está siendo revisada por el responsable asignado. */
    EN_PROCESO,

    /** El responsable ha atendido la solicitud. Puede proceder al cierre. */
    ATENDIDA,

    /** Estado final. La solicitud ha sido cerrada formalmente. */
    CERRADA,

    /** La solicitud fue rechazada durante la revisión. Puede proceder al cierre. */
    RECHAZADA,

    /** Estado final. El solicitante canceló su propia solicitud mientras estaba pendiente. */
    CANCELADA
}