package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;

public interface NotificacionService {

    void notificarSolicitudCreada(Solicitud solicitud, Usuario solicitante);

    void notificarSolicitudClasificada(Solicitud solicitud, Usuario solicitante);

    void notificarResponsableAsignado(Solicitud solicitud, Usuario responsable);

    void notificarSolicitudAtendida(Solicitud solicitud, Usuario solicitante);

    void notificarSolicitudRechazada(Solicitud solicitud, Usuario solicitante);

    void notificarSolicitudCerrada(Solicitud solicitud, Usuario solicitante);

    void notificarSolicitudCancelada(Solicitud solicitud, Usuario solicitante);
}