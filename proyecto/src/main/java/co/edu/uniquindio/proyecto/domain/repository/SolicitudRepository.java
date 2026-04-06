package co.edu.uniquindio.proyecto.domain.repository;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.CodigoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;

import java.util.List;

public interface SolicitudRepository {

    Solicitud obtenerPorCodigo(CodigoSolicitud codigo);

    void guardar(Solicitud solicitud);

    List<Solicitud> obtenerPorEstado(EstadoSolicitud estado);
}