package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de persistencia para el agregado {@code Solicitud}.
 *
 * <p>Convierte entre el modelo de dominio ({@link Solicitud}, {@link EventoHistorial})
 * y el modelo de persistencia ({@link SolicitudEntity}, {@link EventoHistorialEmbeddable}).
 * Está gestionado por MapStruct con el modelo de componente Spring.</p>
 *
 * <p>La conversión de entidad a dominio ({@code toDomain}) recibe el solicitante
 * y responsable ya resueltos desde el repositorio, ya que la entidad solo
 * almacena sus identificadores externos.</p>
 */
@Mapper(componentModel = "spring")
public interface SolicitudPersistenceMapper {

    // ── Domain → Entity ───────────────────────────────────────────────────

    /**
     * Convierte el agregado {@link Solicitud} a su entidad de persistencia.
     *
     * @param solicitud Agregado de dominio a convertir.
     * @return Entidad JPA lista para persistir.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigo", expression = "java(solicitud.getCodigo().valor())")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "tipoSolicitud", expression = "java(solicitud.getTipoSolicitud() != null ? toTipoSolicitudEnum(solicitud.getTipoSolicitud()) : null)")
    @Mapping(target = "prioridad", expression = "java(solicitud.getPrioridad() != null ? toPrioridadEnum(solicitud.getPrioridad()) : null)")
    @Mapping(target = "estado", expression = "java(toEstadoEnum(solicitud.getEstado()))")
    @Mapping(target = "solicitanteCodigo", expression = "java(solicitud.getSolicitante().getId())")
    @Mapping(target = "responsableCodigo", expression = "java(solicitud.getResponsable() != null ? solicitud.getResponsable().getId() : null)")
    @Mapping(target = "historial", expression = "java(toEventoHistorialEmbeddableList(solicitud.getHistorial()))")
    SolicitudEntity toEntity(Solicitud solicitud);

    // ── EventoHistorial → Embeddable ──────────────────────────────────────

    /**
     * Convierte un {@link EventoHistorial} del dominio a su representación embebida.
     *
     * @param evento Evento de historial del dominio.
     * @return Objeto embebido listo para persistir.
     */
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "estadoResultante", expression = "java(toEstadoEnum(evento.estadoResultante()))")
    @Mapping(target = "realizadoPor", source = "realizadoPor")
    @Mapping(target = "fecha", source = "fecha")
    EventoHistorialEmbeddable toEventoEmbeddable(EventoHistorial evento);

    /**
     * Convierte una lista de eventos de historial del dominio a su representación embebida.
     *
     * @param eventos Lista de eventos del dominio.
     * @return Lista de embebidos lista para persistir.
     */
    List<EventoHistorialEmbeddable> toEventoHistorialEmbeddableList(List<EventoHistorial> eventos);

    // ── Entity → Domain ───────────────────────────────────────────────────

    /**
     * Reconstruye el agregado {@link Solicitud} desde su entidad de persistencia.
     *
     * <p>Requiere el solicitante y responsable ya resueltos porque la entidad
     * solo almacena sus identificadores externos, no las entidades completas.</p>
     *
     * @param entity      Entidad JPA leída desde la base de datos.
     * @param solicitante Usuario solicitante ya resuelto desde el repositorio.
     * @param responsable Usuario responsable ya resuelto. Puede ser nulo.
     * @return Agregado de dominio reconstruido.
     */
    default Solicitud toDomain(SolicitudEntity entity, Usuario solicitante, Usuario responsable) {
        if (entity == null) return null;

        CodigoSolicitud codigo = new CodigoSolicitud(entity.getCodigo());
        EstadoSolicitud estado = toEstadoDomain(entity.getEstado());

        TipoSolicitud tipo = entity.getTipoSolicitud() != null
                ? toTipoSolicitudDomain(entity.getTipoSolicitud())
                : null;

        Prioridad prioridad = entity.getPrioridad() != null
                ? toPrioridadDomain(entity.getPrioridad())
                : null;

        List<EventoHistorial> historial = entity.getHistorial().stream()
                .map(this::toEventoHistorialDomain)
                .collect(Collectors.toList());

        return Solicitud.reconstruirDesdeDB(
                entity.getId(),
                codigo,
                descripcion(entity),
                solicitante,
                responsable,
                estado,
                tipo,
                prioridad,
                historial,
                entity.getFechaCreacion()
        );
    }

    /**
     * Convierte un {@link EventoHistorialEmbeddable} de persistencia al value object
     * {@link EventoHistorial} del dominio.
     *
     * @param embeddable Objeto embebido leído desde la base de datos.
     * @return Value object de dominio.
     */
    default EventoHistorial toEventoHistorialDomain(EventoHistorialEmbeddable embeddable) {
        if (embeddable == null) return null;
        return new EventoHistorial(
                embeddable.getDescripcion(),
                toEstadoDomain(embeddable.getEstadoResultante()),
                embeddable.getRealizadoPor(),
                embeddable.getFecha()
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Extrae la descripción de la entidad. Usado en expresiones de mapeo. */
    default String descripcion(SolicitudEntity entity) {
        return entity.getDescripcion();
    }

    // ── Conversiones de enumeradores ──────────────────────────────────────

    /** Convierte {@link EstadoSolicitud} del dominio a su equivalente de persistencia. */
    default EstadoSolicitudEnum toEstadoEnum(EstadoSolicitud estado) {
        if (estado == null) return null;
        return EstadoSolicitudEnum.valueOf(estado.name());
    }

    /** Convierte {@link EstadoSolicitudEnum} de persistencia a su equivalente de dominio. */
    default EstadoSolicitud toEstadoDomain(EstadoSolicitudEnum estado) {
        if (estado == null) return null;
        return EstadoSolicitud.valueOf(estado.name());
    }

    /** Convierte {@link TipoSolicitud} del dominio a su equivalente de persistencia. */
    default TipoSolicitudEnum toTipoSolicitudEnum(TipoSolicitud tipo) {
        if (tipo == null) return null;
        return TipoSolicitudEnum.valueOf(tipo.name());
    }

    /** Convierte {@link TipoSolicitudEnum} de persistencia a su equivalente de dominio. */
    default TipoSolicitud toTipoSolicitudDomain(TipoSolicitudEnum tipo) {
        if (tipo == null) return null;
        return TipoSolicitud.valueOf(tipo.name());
    }

    /** Convierte {@link Prioridad} del dominio a su equivalente de persistencia. */
    default PrioridadEnum toPrioridadEnum(Prioridad prioridad) {
        if (prioridad == null) return null;
        return PrioridadEnum.valueOf(prioridad.name());
    }

    /** Convierte {@link PrioridadEnum} de persistencia a su equivalente de dominio. */
    default Prioridad toPrioridadDomain(PrioridadEnum prioridad) {
        if (prioridad == null) return null;
        return Prioridad.valueOf(prioridad.name());
    }
}