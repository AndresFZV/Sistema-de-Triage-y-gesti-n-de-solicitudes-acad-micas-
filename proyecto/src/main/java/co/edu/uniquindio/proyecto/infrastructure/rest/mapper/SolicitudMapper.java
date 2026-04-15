package co.edu.uniquindio.proyecto.infrastructure.rest.mapper;

import co.edu.uniquindio.proyecto.aplication.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.proyecto.aplication.dto.response.SolicitudResponse;
import co.edu.uniquindio.proyecto.aplication.dto.response.UsuarioResponse;
import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.EventoHistorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    // ---- Solicitud → SolicitudResponse ----

    @Mapping(target = "codigo", expression = "java(solicitud.getCodigo().valor())")
    @Mapping(target = "estado", expression = "java(solicitud.getEstado().name())")
    @Mapping(target = "tipoSolicitud", expression = "java(solicitud.getTipoSolicitud() != null ? solicitud.getTipoSolicitud().name() : null)")
    @Mapping(target = "prioridad", expression = "java(solicitud.getPrioridad() != null ? solicitud.getPrioridad().name() : null)")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "solicitante", source = "solicitante")
    @Mapping(target = "responsable", source = "responsable")
    SolicitudResponse toResponse(Solicitud solicitud);

    List<SolicitudResponse> toResponseList(List<Solicitud> solicitudes);

    // ---- Usuario → UsuarioResponse ----

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "email", expression = "java(usuario.getEmail().valor())")
    @Mapping(target = "tipoUsuario", expression = "java(usuario.getTipoUsuario().name())")
    UsuarioResponse toUsuarioResponse(Usuario usuario);

    // ---- EventoHistorial → EventoHistorialResponse ----

    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "estadoResultante", expression = "java(evento.estadoResultante().name())")
    @Mapping(target = "realizadoPor", source = "realizadoPor")
    @Mapping(target = "fecha", source = "fecha")
    EventoHistorialResponse toEventoResponse(EventoHistorial evento);

    List<EventoHistorialResponse> toEventoResponseList(List<EventoHistorial> eventos);
}