package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesPorEstadoUseCase {

    private final SolicitudRepository solicitudRepository;

    @Transactional(readOnly = true)
    public List<Solicitud> ejecutar(EstadoSolicitud estado) {
        if (estado != null)
            return solicitudRepository.findByEstado(estado);
        return solicitudRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Solicitud> ejecutarPaginado(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            String solicitanteId,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).descending()
        );

        return solicitudRepository.buscarConFiltros(
                estado, tipo, prioridad, solicitanteId, pageable
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Long> reportePorEstado() {
        return solicitudRepository.reportePorEstado();
    }
}