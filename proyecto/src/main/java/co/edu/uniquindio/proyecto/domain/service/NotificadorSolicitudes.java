package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoNotificacion;

import java.util.List;

public class NotificadorSolicitudes {

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