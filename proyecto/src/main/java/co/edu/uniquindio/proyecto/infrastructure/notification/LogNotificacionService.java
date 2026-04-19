package co.edu.uniquindio.proyecto.infrastructure.notification;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogNotificacionService implements NotificacionService {

    @Override
    public void notificarSolicitudCreada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud creada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    @Override
    public void notificarSolicitudClasificada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud clasificada — codigo: {}, tipo: {}, prioridad: {}, solicitante: {}",
                solicitud.getCodigo().valor(),
                solicitud.getTipoSolicitud(),
                solicitud.getPrioridad(),
                solicitante.getNombre());
    }

    @Override
    public void notificarResponsableAsignado(Solicitud solicitud, Usuario responsable) {
        log.info("[NOTIFICACION] Responsable asignado — codigo: {}, responsable: {}, email: {}",
                solicitud.getCodigo().valor(),
                responsable.getNombre(),
                responsable.getEmail().valor());
    }

    @Override
    public void notificarSolicitudAtendida(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud atendida — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    @Override
    public void notificarSolicitudRechazada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud rechazada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    @Override
    public void notificarSolicitudCerrada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud cerrada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    @Override
    public void notificarSolicitudCancelada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud cancelada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }
}