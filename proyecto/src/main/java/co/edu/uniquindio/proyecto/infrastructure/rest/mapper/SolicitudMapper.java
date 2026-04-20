package co.edu.uniquindio.proyecto.infrastructure.rest.mapper;

import co.edu.uniquindio.proyecto.application.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.proyecto.application.dto.response.SolicitudResponse;
import co.edu.uniquindio.proyecto.application.dto.response.UsuarioResponse;
import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.EventoHistorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper de infraestructura encargado de transformar objetos del dominio
 * en DTOs de respuesta para la capa REST.
 *
 * <p>Utiliza MapStruct para generar automáticamente las implementaciones
 * de conversión entre entidades del dominio y DTOs.</p>
 *
 * <p>Se encarga de mapear:</p>
 * <ul>
 *     <li>{@link Solicitud} → {@link SolicitudResponse}</li>
 *     <li>{@link Usuario} → {@link UsuarioResponse}</li>
 *     <li>{@link EventoHistorial} → {@link EventoHistorialResponse}</li>
 * </ul>
 *
 * <p>Incluye conversiones personalizadas para atributos que son Value Objects,
 * enums o estructuras complejas del dominio.</p>
 */
@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    /**
     * Convierte una entidad de dominio {@link Solicitud} en un DTO de respuesta.
     *
     * <p>Realiza transformaciones explícitas para:</p>
     * <ul>
     *     <li>Extraer el valor del código</li>
     *     <li>Convertir enums a String</li>
     *     <li>Mapear objetos relacionados como solicitante y responsable</li>
     * </ul>
     *
     * @param solicitud Entidad de dominio a convertir.
     * @return DTO de respuesta {@link SolicitudResponse}.
     */
    @Mapping(target = "codigo", expression = "java(solicitud.getCodigo().valor())")
    @Mapping(target = "estado", expression = "java(solicitud.getEstado().name())")
    @Mapping(target = "tipoSolicitud", expression = "java(solicitud.getTipoSolicitud() != null ? solicitud.getTipoSolicitud().name() : null)")
    @Mapping(target = "prioridad", expression = "java(solicitud.getPrioridad() != null ? solicitud.getPrioridad().name() : null)")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "solicitante", source = "solicitante")
    @Mapping(target = "responsable", source = "responsable")
    SolicitudResponse toResponse(Solicitud solicitud);

    /**
     * Convierte una lista de solicitudes del dominio en una lista de DTOs.
     *
     * @param solicitudes Lista de solicitudes.
     * @return Lista de {@link SolicitudResponse}.
     */
    List<SolicitudResponse> toResponseList(List<Solicitud> solicitudes);

    /**
     * Convierte una entidad {@link Usuario} en un DTO de respuesta.
     *
     * <p>Transforma el email desde su Value Object a String
     * y el tipo de usuario a su representación textual.</p>
     *
     * @param usuario Usuario del dominio.
     * @return DTO {@link UsuarioResponse}.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "email", expression = "java(usuario.getEmail().valor())")
    @Mapping(target = "tipoUsuario", expression = "java(usuario.getTipoUsuario().name())")
    UsuarioResponse toUsuarioResponse(Usuario usuario);

    /**
     * Convierte un evento del historial en un DTO de respuesta.
     *
     * <p>Incluye la transformación del estado resultante a String.</p>
     *
     * @param evento Evento del historial.
     * @return DTO {@link EventoHistorialResponse}.
     */
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "estadoResultante", expression = "java(evento.estadoResultante().name())")
    @Mapping(target = "realizadoPor", source = "realizadoPor")
    @Mapping(target = "fecha", source = "fecha")
    EventoHistorialResponse toEventoResponse(EventoHistorial evento);

    /**
     * Convierte una lista de eventos del historial en DTOs de respuesta.
     *
     * @param eventos Lista de eventos.
     * @return Lista de {@link EventoHistorialResponse}.
     */
    List<EventoHistorialResponse> toEventoResponseList(List<EventoHistorial> eventos);
}