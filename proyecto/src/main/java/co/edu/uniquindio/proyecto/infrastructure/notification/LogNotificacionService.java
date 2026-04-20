package co.edu.uniquindio.proyecto.infrastructure.notification;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de notificaciones basada en logging.
 *
 * <p>Actúa como adaptador de infraestructura del contrato
 * {@link NotificacionService} definido en el dominio. En lugar de
 * enviar correos o mensajes reales, registra cada evento en el log
 * del sistema usando SLF4J.</p>
 *
 * <p>Esta implementación es ideal para desarrollo y pruebas. En producción
 * puede reemplazarse por una implementación que use un servicio de email
 * o mensajería sin modificar ninguna clase del dominio ni de la capa
 * de aplicación, gracias a la inversión de dependencias.</p>
 */
@Slf4j
@Service
public class LogNotificacionService implements NotificacionService {

    /**
     * Notifica que una nueva solicitud fue registrada exitosamente.
     *
     * @param solicitud  Solicitud recién creada.
     * @param solicitante Usuario que registró la solicitud.
     */
    @Override
    public void notificarSolicitudCreada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud creada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    /**
     * Notifica que una solicitud fue clasificada con tipo y prioridad asignados.
     *
     * @param solicitud   Solicitud clasificada.
     * @param solicitante Usuario que registró la solicitud.
     */
    @Override
    public void notificarSolicitudClasificada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud clasificada — codigo: {}, tipo: {}, prioridad: {}, solicitante: {}",
                solicitud.getCodigo().valor(),
                solicitud.getTipoSolicitud(),
                solicitud.getPrioridad(),
                solicitante.getNombre());
    }

    /**
     * Notifica al responsable que fue asignado a una solicitud.
     *
     * @param solicitud   Solicitud puesta en revisión.
     * @param responsable Administrativo asignado como responsable.
     */
    @Override
    public void notificarResponsableAsignado(Solicitud solicitud, Usuario responsable) {
        log.info("[NOTIFICACION] Responsable asignado — codigo: {}, responsable: {}, email: {}",
                solicitud.getCodigo().valor(),
                responsable.getNombre(),
                responsable.getEmail().valor());
    }

    /**
     * Notifica al solicitante que su solicitud fue atendida.
     *
     * @param solicitud   Solicitud marcada como atendida.
     * @param solicitante Usuario que registró la solicitud.
     */
    @Override
    public void notificarSolicitudAtendida(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud atendida — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    /**
     * Notifica al solicitante que su solicitud fue rechazada durante la revisión.
     *
     * @param solicitud   Solicitud rechazada.
     * @param solicitante Usuario que registró la solicitud.
     */
    @Override
    public void notificarSolicitudRechazada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud rechazada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    /**
     * Notifica al solicitante que su solicitud fue cerrada definitivamente.
     *
     * @param solicitud   Solicitud cerrada.
     * @param solicitante Usuario que registró la solicitud.
     */
    @Override
    public void notificarSolicitudCerrada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud cerrada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }

    /**
     * Notifica al solicitante que su solicitud fue cancelada.
     *
     * @param solicitud   Solicitud cancelada.
     * @param solicitante Usuario que canceló la solicitud.
     */
    @Override
    public void notificarSolicitudCancelada(Solicitud solicitud, Usuario solicitante) {
        log.info("[NOTIFICACION] Solicitud cancelada — codigo: {}, solicitante: {}, email: {}",
                solicitud.getCodigo().valor(),
                solicitante.getNombre(),
                solicitante.getEmail().valor());
    }
}