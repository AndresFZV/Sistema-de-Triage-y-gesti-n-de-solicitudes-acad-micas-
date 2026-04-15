package co.edu.uniquindio.proyecto.domain.repository;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository {

    Solicitud save(Solicitud solicitud);

    Optional<Solicitud> findById(String id);

    Optional<Solicitud> findByCodigo(String codigo);

    List<Solicitud> findByEstado(EstadoSolicitud estado);

    List<Solicitud> findAll();
}