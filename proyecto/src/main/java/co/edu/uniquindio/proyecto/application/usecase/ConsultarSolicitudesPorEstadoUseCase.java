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

/**
 * Caso de uso para consultar solicitudes con múltiples criterios y generar reportes.
 *
 * <p>Agrupa todas las operaciones de consulta sobre el agregado {@code Solicitud}:
 * filtros por estado, paginación, reportes estadísticos y construcción del
 * dashboard administrativo. Al ser operaciones de solo lectura están marcadas
 * con {@code readOnly = true} para optimizar el rendimiento.</p>
 */
@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesPorEstadoUseCase {

    private final SolicitudRepository solicitudRepository;

    /**
     * Retorna todas las solicitudes o filtra por estado si se indica uno.
     *
     * @param estado Estado por el que filtrar. Si es nulo retorna todas.
     * @return Lista de solicitudes que cumplen el criterio.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> ejecutar(EstadoSolicitud estado) {
        if (estado != null)
            return solicitudRepository.findByEstado(estado);
        return solicitudRepository.findAll();
    }

    /**
     * Retorna una página de solicitudes aplicando filtros combinados opcionales.
     *
     * @param estado        Filtra por estado. Opcional.
     * @param tipo          Filtra por tipo de solicitud. Opcional.
     * @param prioridad     Filtra por nivel de prioridad. Opcional.
     * @param solicitanteId Filtra por solicitante. Opcional.
     * @param page          Número de página (0-indexed).
     * @param size          Cantidad de elementos por página.
     * @param sortBy        Campo por el que ordenar descendentemente.
     * @return Página de solicitudes que cumplen los filtros.
     */
    @Transactional(readOnly = true)
    public Page<Solicitud> ejecutarPaginado(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            String solicitanteId,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return solicitudRepository.buscarConFiltros(estado, tipo, prioridad, solicitanteId, pageable);
    }

    /**
     * Retorna un mapa con el conteo de solicitudes agrupadas por estado.
     *
     * @return Mapa donde la clave es el nombre del estado y el valor es la cantidad.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorEstado() {
        return solicitudRepository.reportePorEstado();
    }

    /**
     * Retorna las solicitudes en estado PENDIENTE sin responsable asignado.
     *
     * @return Lista de solicitudes pendientes sin responsable.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarPendientesSinResponsable() {
        return solicitudRepository.findPendientesSinResponsable();
    }

    /**
     * Retorna las solicitudes asignadas a un responsable específico.
     *
     * @param responsableId Identificador del administrativo responsable.
     * @return Lista de solicitudes asignadas al responsable.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarAsignadasA(String responsableId) {
        return solicitudRepository.findByResponsableId(responsableId);
    }

    /**
     * Retorna solicitudes que llevan más días sin atenderse que el límite indicado.
     *
     * @param diasLimite Número de días máximo sin atención.
     * @return Lista de solicitudes vencidas.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarVencidas(int diasLimite) {
        return solicitudRepository.findVencidas(diasLimite);
    }

    /**
     * Retorna un mapa con el conteo de solicitudes agrupadas por tipo.
     *
     * @return Mapa donde la clave es el nombre del tipo y el valor es la cantidad.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorTipo() {
        return solicitudRepository.reportePorTipo();
    }

    /**
     * Retorna un mapa con el conteo de solicitudes agrupadas por responsable.
     *
     * @return Mapa donde la clave es el identificador del responsable y el valor es la cantidad.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorResponsable() {
        return solicitudRepository.reportePorResponsable();
    }

    /**
     * Construye el dashboard administrativo consolidando estadísticas generales.
     *
     * <p>Combina el reporte por estado y por tipo para ofrecer una vista
     * completa del estado actual del sistema en una sola llamada.</p>
     *
     * @return DTO con todos los indicadores del dashboard.
     */
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