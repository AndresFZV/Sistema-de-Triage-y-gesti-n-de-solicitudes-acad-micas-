package co.edu.uniquindio.proyecto.infrastructure.rest;

import co.edu.uniquindio.proyecto.aplication.dto.request.*;
import co.edu.uniquindio.proyecto.aplication.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.proyecto.aplication.dto.response.SolicitudResponse;
import co.edu.uniquindio.proyecto.aplication.usecase.*;
import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import co.edu.uniquindio.proyecto.infrastructure.rest.mapper.SolicitudMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Gestión del ciclo de vida de solicitudes académicas")
public class SolicitudController {

    private final CrearSolicitudUseCase crearSolicitudUseCase;
    private final ClasificarSolicitudUseCase clasificarSolicitudUseCase;
    private final EnRevisionUseCase enRevisionUseCase;
    private final AtenderSolicitudUseCase atenderSolicitudUseCase;
    private final RechazarSolicitudUseCase rechazarSolicitudUseCase;
    private final CerrarSolicitudUseCase cerrarSolicitudUseCase;
    private final CancelarSolicitudUseCase cancelarSolicitudUseCase;
    private final ConsultarSolicitudesPorEstadoUseCase consultarPorEstadoUseCase;
    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper mapper;

    @PostMapping
    @Operation(
            summary = "Registrar nueva solicitud",
            description = "Cualquier usuario puede registrar una solicitud. El estado inicial es CLASIFICACION."
    )
    @ApiResponse(responseCode = "201", description = "Solicitud registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "El solicitante no existe")
    public ResponseEntity<SolicitudResponse> registrar(
            @Valid @RequestBody CrearSolicitudRequest request) {

        Solicitud solicitud = crearSolicitudUseCase.ejecutar(
                request.descripcion(),
                request.solicitanteId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(solicitud.getCodigo().valor())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(solicitud));
    }

    @GetMapping
    @Operation(
            summary = "Listar solicitudes",
            description = "Lista solicitudes con filtro opcional por estado."
    )
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResponse>> listar(
            @RequestParam(required = false) EstadoSolicitud estado) {

        List<Solicitud> solicitudes = consultarPorEstadoUseCase.ejecutar(estado);
        return ResponseEntity.ok(mapper.toResponseList(solicitudes));
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener solicitud por código")
    @ApiResponse(responseCode = "200", description = "Solicitud encontrada")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<SolicitudResponse> obtener(@PathVariable String codigo) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @PatchMapping("/{codigo}/clasificar")
    @Operation(
            summary = "Clasificar solicitud",
            description = "Asigna un tipo. La prioridad se calcula automáticamente. Solo administrativos. Estado requerido: CLASIFICACION."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud clasificada — estado cambia a PENDIENTE")
    @ApiResponse(responseCode = "403", description = "No es administrativo")
    @ApiResponse(responseCode = "422", description = "Violación de regla de negocio")
    public ResponseEntity<SolicitudResponse> clasificar(
            @PathVariable String codigo,
            @Valid @RequestBody ClasificarSolicitudRequest request) {

        Solicitud solicitud = clasificarSolicitudUseCase.ejecutar(
                codigo,
                TipoSolicitud.valueOf(request.tipoSolicitud()),
                request.adminId()
        );
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @PatchMapping("/{codigo}/revision")
    @Operation(
            summary = "Poner solicitud en revisión",
            description = "Asigna responsable y pone en revisión. Solo administrativos. Estado requerido: PENDIENTE."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud en revisión — estado cambia a EN_PROCESO")
    @ApiResponse(responseCode = "403", description = "No es administrativo")
    @ApiResponse(responseCode = "422", description = "Violación de regla de negocio")
    public ResponseEntity<SolicitudResponse> enRevision(
            @PathVariable String codigo,
            @Valid @RequestBody EnRevisionRequest request) {

        Solicitud solicitud = enRevisionUseCase.ejecutar(codigo, request.responsableId());
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @PatchMapping("/{codigo}/atender")
    @Operation(
            summary = "Marcar solicitud como atendida",
            description = "Solo administrativos. Estado requerido: EN_PROCESO."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud atendida — estado cambia a ATENDIDA")
    @ApiResponse(responseCode = "403", description = "No es administrativo")
    @ApiResponse(responseCode = "422", description = "Violación de regla de negocio")
    public ResponseEntity<SolicitudResponse> atender(
            @PathVariable String codigo,
            @Valid @RequestBody AtenderRequest request) {

        Solicitud solicitud = atenderSolicitudUseCase.ejecutar(codigo, request.adminId());
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @PatchMapping("/{codigo}/rechazar")
    @Operation(
            summary = "Rechazar solicitud",
            description = "Solo administrativos. Estado requerido: EN_PROCESO."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud rechazada — estado cambia a RECHAZADA")
    @ApiResponse(responseCode = "403", description = "No es administrativo")
    @ApiResponse(responseCode = "422", description = "Violación de regla de negocio")
    public ResponseEntity<SolicitudResponse> rechazar(
            @PathVariable String codigo,
            @Valid @RequestBody RechazarRequest request) {

        Solicitud solicitud = rechazarSolicitudUseCase.ejecutar(codigo, request.adminId());
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @PatchMapping("/{codigo}/cerrar")
    @Operation(
            summary = "Cerrar solicitud",
            description = "Solo administrativos. Estado requerido: ATENDIDA o RECHAZADA."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud cerrada — estado cambia a CERRADA")
    @ApiResponse(responseCode = "403", description = "No es administrativo")
    @ApiResponse(responseCode = "422", description = "Violación de regla de negocio")
    public ResponseEntity<SolicitudResponse> cerrar(
            @PathVariable String codigo,
            @Valid @RequestBody CerrarRequest request) {

        Solicitud solicitud = cerrarSolicitudUseCase.ejecutar(codigo, request.adminId());
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @PatchMapping("/{codigo}/cancelar")
    @Operation(
            summary = "Cancelar solicitud",
            description = "Solo el solicitante original puede cancelar. Estado requerido: PENDIENTE."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud cancelada — estado cambia a CANCELADA")
    @ApiResponse(responseCode = "403", description = "No es el solicitante original")
    @ApiResponse(responseCode = "422", description = "Violación de regla de negocio")
    public ResponseEntity<SolicitudResponse> cancelar(
            @PathVariable String codigo,
            @Valid @RequestBody CancelarRequest request) {

        Solicitud solicitud = cancelarSolicitudUseCase.ejecutar(codigo, request.solicitanteId());
        return ResponseEntity.ok(mapper.toResponse(solicitud));
    }

    @GetMapping("/{codigo}/historial")
    @Operation(summary = "Obtener historial de una solicitud")
    @ApiResponse(responseCode = "200", description = "Historial de eventos")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<List<EventoHistorialResponse>> historial(
            @PathVariable String codigo) {

        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));
        return ResponseEntity.ok(mapper.toEventoResponseList(solicitud.getHistorial()));
    }
}