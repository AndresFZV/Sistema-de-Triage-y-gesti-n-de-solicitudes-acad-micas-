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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SolicitudJpaRepository implements SolicitudRepository {

    private final SolicitudJpaDataRepository dataRepository;
    private final SolicitudPersistenceMapper mapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        SolicitudEntity entity = mapper.toEntity(solicitud);
        SolicitudEntity saved = dataRepository.save(entity);
        return toDomainWithRelations(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findById(String id) {
        return dataRepository.findByCodigo(id)
                .map(this::toDomainWithRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findByCodigo(String codigo) {
        return dataRepository.buscarSolicitudConHistorial(codigo)
                .map(this::toDomainWithRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        EstadoSolicitudEnum estadoEnum = EstadoSolicitudEnum.valueOf(estado.name());
        return dataRepository.findByEstado(estadoEnum).stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findAll() {
        return dataRepository.findAll().stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }


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

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorEstado() {
        List<Object[]> resultado = dataRepository.reporteAgrupacionPorEstado();
        Map<String, Long> reporte = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            String estado = fila[0].toString();
            Long total = ((Number) fila[1]).longValue();
            reporte.put(estado, total);
        }
        return reporte;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findBySolicitanteId(String solicitanteId) {
        return dataRepository.findBySolicitanteCodigo(solicitanteId)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findPendientesSinResponsable() {
        return dataRepository
                .findByResponsableCodigoIsNullAndEstado(EstadoSolicitudEnum.PENDIENTE)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByResponsableId(String responsableId) {
        return dataRepository.findByResponsableCodigo(responsableId)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findVencidas(int diasLimite) {
        java.time.LocalDateTime fechaLimite = java.time.LocalDateTime.now()
                .minusDays(diasLimite);
        List<EstadoSolicitudEnum> estados = List.of(
                EstadoSolicitudEnum.PENDIENTE,
                EstadoSolicitudEnum.EN_PROCESO
        );
        return dataRepository.findVencidas(estados, fechaLimite)
                .stream()
                .map(this::toDomainWithRelations)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorTipo() {
        List<Object[]> resultado = dataRepository.reporteAgrupacionPorTipo();
        Map<String, Long> reporte = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            String tipo = fila[0] != null ? fila[0].toString() : "SIN_TIPO";
            Long total = ((Number) fila[1]).longValue();
            reporte.put(tipo, total);
        }
        return reporte;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> reportePorResponsable() {
        List<Object[]> resultado = dataRepository.reporteAgrupacionPorResponsable();
        Map<String, Long> reporte = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            String responsable = fila[0].toString();
            Long total = ((Number) fila[1]).longValue();
            reporte.put(responsable, total);
        }
        return reporte;
    }


}