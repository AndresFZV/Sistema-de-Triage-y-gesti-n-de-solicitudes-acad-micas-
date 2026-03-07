package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;

public class GestorSolicitudService {

    private final AsignadorPrioridadService asignadorPrioridad;

    public GestorSolicitudService() {
        this.asignadorPrioridad = new AsignadorPrioridadService();
    }

    public Solicitud registrar(String descripcion, Usuario solicitante) {
        return new Solicitud(descripcion, solicitante);
    }

    // La prioridad ya no viene de afuera, la calcula el sistema
    public void clasificar(Solicitud solicitud, TipoSolicitud tipo, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede clasificar una solicitud");

        Prioridad prioridad = asignadorPrioridad.asignar(tipo, solicitud.getFechaCreacion());
        solicitud.clasificar(tipo, prioridad);
    }

    public void enRevision(Solicitud solicitud, Usuario responsable) {
        if (!responsable.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede poner en revisión una solicitud");

        solicitud.enRevision(responsable);
    }

    public void atendida(Solicitud solicitud, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede marcar una solicitud como atendida");

        solicitud.atendida();
    }

    public void rechazar(Solicitud solicitud, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede rechazar una solicitud");

        solicitud.rechazar();
    }

    public void cerrar(Solicitud solicitud, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede cerrar una solicitud");

        solicitud.cerrar();
    }

    public void cancelar(Solicitud solicitud, Usuario quien) {
        solicitud.cancelar(quien);
    }
}