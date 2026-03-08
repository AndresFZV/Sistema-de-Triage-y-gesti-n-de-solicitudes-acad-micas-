package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.service.NotificadorSolicitudes;

/**
 * Value Object que categoriza el tipo de evento que dispara una notificación.
 * Es usado por {@link NotificadorSolicitudes} para determinar
 * a quién se debe notificar según el evento ocurrido.
 */
public enum TipoNotificacion {

    /** Se dispara cuando una solicitud es registrada por primera vez. */
    NUEVA_SOLICITUD,

    /** Se dispara cuando un responsable es asignado a la solicitud. */
    ASIGNACION,

    /** Se dispara cuando el estado de la solicitud cambia. */
    CAMBIO_ESTADO,

    /** Se dispara cuando la solicitud es cerrada definitivamente. */
    CIERRE
}