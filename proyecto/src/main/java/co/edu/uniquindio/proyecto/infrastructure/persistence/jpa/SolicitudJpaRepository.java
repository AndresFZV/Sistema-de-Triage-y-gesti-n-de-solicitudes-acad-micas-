package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.EstadoSolicitudEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.PrioridadEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.SolicitudEntity;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoSolicitudEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper.SolicitudPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia que implementa {@link SolicitudRepository}.
 *
 * <p>Actúa como puente entre la capa de dominio y la base de datos relacional.
 * Traduce entre objetos de dominio ({@link Solicitud}) y entidades JPA
 * ({@link SolicitudEntity}) usando {@link SolicitudPersistenceMapper}.</p>
 *
 * <p>Resuelve las relaciones con {@link Usuario} antes de reconstruir
 * el agregado, ya que la entidad solo almacena identificadores externos.</p>
 */
@Repository
@RequiredArgsConstructor
public class SolicitudJpaRepository implements SolicitudRepository {

    private final SolicitudJpaDataRepository dataRepository;
    private final SolicitudPersistenceMapper mapper;
    private final UsuarioRepository usuarioRepository;

    /**
     * Persiste o actualiza una solicitud en la base de datos.
     *
     * @param solicitud Agregado de dominio a persistir.
     * @return Solicitud guardada con sus relaciones resueltas.
     */
    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        SolicitudEntity entity = mapper.toEntity(solicitud);
        SolicitudEntity saved = dataRepository.save(entity);
        return toDomainWithRelations(saved);
    }

    /**
     * Busca una solicitud por su código de negocio usado como identificador externo.
     *
     * @param id Código único de la solicitud.
     * @return Optional con la solicitud si existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findById(String id) {
        return dataRepository.findByCodigo(id)
                .map(this::toDomainWithRelations);
    }

    /**
     * Busca una solicitud por código cargando su historial en la misma consulta
     * para evitar el problema N+1.
     *
     * @param codigo Código único de la solicitud.
     * @return Optional con la solicitud y su historial completo.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findByCodigo(String codigo) {
        return dataRepository.buscarSolicitudConHistorial(codigo)
                .map(this::toDomainWithRelations);
    }

    /**
     * Retorna todas las solicitudes con el estado indicado.
     *
     * @param estado Estado por el que filtrar.
     * @return Lista de solicitudes en ese estado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        EstadoSolicitudEnum estadoEnum = EstadoSolicitudEnum.valueOf(estado.name());
        return dataRepository.findByEstado(estadoEnum).stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas las solicitudes registradas en el sistema.
     *
     * @return Lista completa de solicitudes.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findAll() {
        return dataRepository.findAll().stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }

    /**
     * Busca solicitudes aplicando filtros opcionales combinados con paginación.
     *
     * @param estado        Filtra por estado. Opcional.
     * @param tipo          Filtra por tipo. Opcional.
     * @param prioridad     Filtra por prioridad. Opcional.
     * @param solicitanteId Filtra por solicitante. Opcional.
     * @param pageable      Configuración de paginación y ordenamiento.
     * @return Página de solicitudes que cumplen los filtros.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Solicitud> buscarConFiltros(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            String solicitanteId,
            Pageable pageable) {

        EstadoSolicitudEnum estadoEnum = estado != null
                ? EstadoSolicitudEnum.valueOf(estado.name()) : null;
        TipoSolicitudEnum tipoEnum = tipo != null
                ? TipoSolicitudEnum.valueOf(tipo.name()) : null;
        PrioridadEnum prioridadEnum = prioridad != null
                ? PrioridadEnum.valueOf(prioridad.name()) : null;

        return dataRepository
                .buscarConFiltros(estadoEnum, tipoEnum, prioridadEnum, solicitanteId, pageable)
                .map(this::toDomainWithRelations);
    }

    /**
     * Retorna el conteo de solicitudes agrupadas por estado.
     *
     * @return Mapa con el nombre del estado como clave y el conteo como valor.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorEstado() {
        List<Object[]> resultado = dataRepository.reporteAgrupacionPorEstado();
        Map<String, Long> reporte = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            reporte.put(fila[0].toString(), ((Number) fila[1]).longValue());
        }
        return reporte;
    }

    /**
     * Retorna todas las solicitudes registradas por el solicitante indicado.
     *
     * @param solicitanteId Identificador externo del solicitante.
     * @return Lista de solicitudes del solicitante.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findBySolicitanteId(String solicitanteId) {
        return dataRepository.findBySolicitanteCodigo(solicitanteId)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    /**
     * Retorna las solicitudes en estado PENDIENTE sin responsable asignado.
     *
     * @return Lista de solicitudes pendientes sin responsable.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findPendientesSinResponsable() {
        return dataRepository
                .findByResponsableCodigoIsNullAndEstado(EstadoSolicitudEnum.PENDIENTE)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    /**
     * Retorna las solicitudes asignadas al responsable indicado.
     *
     * @param responsableId Identificador externo del administrativo responsable.
     * @return Lista de solicitudes asignadas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByResponsableId(String responsableId) {
        return dataRepository.findByResponsableCodigo(responsableId)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    /**
     * Retorna solicitudes que llevan más días sin atenderse que el límite indicado.
     *
     * @param diasLimite Número de días máximo sin atención.
     * @return Lista de solicitudes vencidas en estado PENDIENTE o EN_PROCESO.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findVencidas(int diasLimite) {
        java.time.LocalDateTime fechaLimite = java.time.LocalDateTime.now().minusDays(diasLimite);
        List<EstadoSolicitudEnum> estados = List.of(
                EstadoSolicitudEnum.PENDIENTE,
                EstadoSolicitudEnum.EN_PROCESO
        );
        return dataRepository.findVencidas(estados, fechaLimite)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    /**
     * Retorna el conteo de solicitudes agrupadas por tipo.
     *
     * @return Mapa con el nombre del tipo como clave y el conteo como valor.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorTipo() {
        List<Object[]> resultado = dataRepository.reporteAgrupacionPorTipo();
        Map<String, Long> reporte = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            String tipo = fila[0] != null ? fila[0].toString() : "SIN_TIPO";
            reporte.put(tipo, ((Number) fila[1]).longValue());
        }
        return reporte;
    }

    /**
     * Retorna el conteo de solicitudes agrupadas por responsable.
     *
     * @return Mapa con el identificador del responsable como clave y el conteo como valor.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorResponsable() {
        List<Object[]> resultado = dataRepository.reporteAgrupacionPorResponsable();
        Map<String, Long> reporte = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            reporte.put(fila[0].toString(), ((Number) fila[1]).longValue());
        }
        return reporte;
    }

    // ── Privado ───────────────────────────────────────────────────────────

    /**
     * Reconstruye el agregado {@link Solicitud} resolviendo sus relaciones
     * con {@link Usuario} antes de delegar al mapper.
     *
     * @param entity Entidad JPA leída desde la base de datos.
     * @return Agregado de dominio con solicitante y responsable resueltos.
     * @throws UsuarioNoEncontradoException si el solicitante no existe.
     */
    private Solicitud toDomainWithRelations(SolicitudEntity entity) {
        Usuario solicitante = usuarioRepository
                .findById(entity.getSolicitanteCodigo())
                .orElseThrow(() -> new UsuarioNoEncontradoException(entity.getSolicitanteCodigo()));

        Usuario responsable = null;
        if (entity.getResponsableCodigo() != null) {
            responsable = usuarioRepository
                    .findById(entity.getResponsableCodigo())
                    .orElse(null);
        }

        return mapper.toDomain(entity, solicitante, responsable);
    }
}