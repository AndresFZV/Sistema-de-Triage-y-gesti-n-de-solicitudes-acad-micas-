package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoNotificacion;

import java.util.List;

/**
 * Servicio de dominio que determina a quién se debe notificar
 * según el tipo de evento ocurrido sobre una solicitud.
 *
 * <p>Coordina información del solicitante y el responsable sin pertenecer
 * naturalmente a ninguna de las dos entidades. Por eso vive como
 * servicio de dominio independiente.</p>
 *
 * <p>Reglas de notificación:</p>
 * <ul>
 *   <li>{@link TipoNotificacion#NUEVA_SOLICITUD} → solo el solicitante.</li>
 *   <li>{@link TipoNotificacion#ASIGNACION} → solicitante y responsable.</li>
 *   <li>{@link TipoNotificacion#CAMBIO_ESTADO} → solo el solicitante.</li>
 *   <li>{@link TipoNotificacion#CIERRE} → solicitante y responsable.</li>
 * </ul>
 */
public class NotificadorSolicitudes {

    /**
     * Determina la lista de emails a notificar según el tipo de evento.
     *
     * @param solicitud La solicitud sobre la que ocurrió el evento.
     * @param tipo      El tipo de notificación a enviar.
     * @return Lista de direcciones de email de los destinatarios.
     */
    public List<String> determinarDestinatarios(Solicitud solicitud, TipoNotificacion tipo) {
        return switch (tipo) {
            case NUEVA_SOLICITUD -> List.of(
                    solicitud.getSolicitante().getEmail().valor()
            );
            case ASIGNACION -> List.of(
                    solicitud.getSolicitante().getEmail().valor(),
                    solicitud.getResponsable().getEmail().valor()
            );
            case CAMBIO_ESTADO -> List.of(
                    solicitud.getSolicitante().getEmail().valor()
            );
            case CIERRE -> List.of(
                    solicitud.getSolicitante().getEmail().valor(),
                    solicitud.getResponsable().getEmail().valor()
            );
        };
    }
}