package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SolicitudPersistenceMapper {

    // ---- Domain → Entity ----

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

    // ---- EventoHistorial → Embeddable ----

    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "estadoResultante", expression = "java(toEstadoEnum(evento.estadoResultante()))")
    @Mapping(target = "realizadoPor", source = "realizadoPor")
    @Mapping(target = "fecha", source = "fecha")
    EventoHistorialEmbeddable toEventoEmbeddable(EventoHistorial evento);

    List<EventoHistorialEmbeddable> toEventoHistorialEmbeddableList(List<EventoHistorial> eventos);

    // ---- Entity → Domain ----

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

    default EventoHistorial toEventoHistorialDomain(EventoHistorialEmbeddable embeddable) {
        if (embeddable == null) return null;
        return new EventoHistorial(
                embeddable.getDescripcion(),
                toEstadoDomain(embeddable.getEstadoResultante()),
                embeddable.getRealizadoPor(),
                embeddable.getFecha()
        );
    }

    // ---- Helpers ----

    default String descripcion(SolicitudEntity entity) {
        return entity.getDescripcion();
    }

    // ---- Enum conversions ----

    default EstadoSolicitudEnum toEstadoEnum(EstadoSolicitud estado) {
        if (estado == null) return null;
        return EstadoSolicitudEnum.valueOf(estado.name());
    }

    default EstadoSolicitud toEstadoDomain(EstadoSolicitudEnum estado) {
        if (estado == null) return null;
        return EstadoSolicitud.valueOf(estado.name());
    }

    default TipoSolicitudEnum toTipoSolicitudEnum(TipoSolicitud tipo) {
        if (tipo == null) return null;
        return TipoSolicitudEnum.valueOf(tipo.name());
    }

    default TipoSolicitud toTipoSolicitudDomain(TipoSolicitudEnum tipo) {
        if (tipo == null) return null;
        return TipoSolicitud.valueOf(tipo.name());
    }

    default PrioridadEnum toPrioridadEnum(Prioridad prioridad) {
        if (prioridad == null) return null;
        return PrioridadEnum.valueOf(prioridad.name());
    }

    default Prioridad toPrioridadDomain(PrioridadEnum prioridad) {
        if (prioridad == null) return null;
        return Prioridad.valueOf(prioridad.name());
    }
}