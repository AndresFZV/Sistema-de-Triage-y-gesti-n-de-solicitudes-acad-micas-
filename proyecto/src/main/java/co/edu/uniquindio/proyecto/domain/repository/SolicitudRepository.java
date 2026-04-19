package co.edu.uniquindio.proyecto.domain.repository;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SolicitudRepository {

    Solicitud save(Solicitud solicitud);

    Optional<Solicitud> findById(String id);

    Optional<Solicitud> findByCodigo(String codigo);

    List<Solicitud> findByEstado(EstadoSolicitud estado);

    List<Solicitud> findAll();

    Page<Solicitud> buscarConFiltros(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            String solicitanteId,
            Pageable pageable
    );

    Map<String, Long> reportePorEstado();

    List<Solicitud> findBySolicitanteId(String solicitanteId);
}