package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.application.dto.response.DashboardResponse;
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

    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarPendientesSinResponsable() {
        return solicitudRepository.findPendientesSinResponsable();
    }

    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarAsignadasA(String responsableId) {
        return solicitudRepository.findByResponsableId(responsableId);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarVencidas(int diasLimite) {
        return solicitudRepository.findVencidas(diasLimite);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> reportePorTipo() {
        return solicitudRepository.reportePorTipo();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> reportePorResponsable() {
        return solicitudRepository.reportePorResponsable();
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        Map<String, Long> porEstado = solicitudRepository.reportePorEstado();
        Map<String, Long> porTipo = solicitudRepository.reportePorTipo();

        return new DashboardResponse(
                porEstado.values().stream().mapToLong(Long::longValue).sum(),
                porEstado.getOrDefault("PENDIENTE", 0L),
                porEstado.getOrDefault("EN_PROCESO", 0L),
                porEstado.getOrDefault("ATENDIDA", 0L),
                porEstado.getOrDefault("RECHAZADA", 0L),
                porEstado.getOrDefault("CERRADA", 0L),
                porEstado.getOrDefault("CANCELADA", 0L),
                solicitudRepository.findPendientesSinResponsable().size(),
                porTipo
        );
    }
}