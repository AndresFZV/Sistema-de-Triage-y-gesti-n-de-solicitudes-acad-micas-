package co.edu.uniquindio.proyecto.infrastructure.rest.controllers;

import co.edu.uniquindio.proyecto.application.dto.request.*;
import co.edu.uniquindio.proyecto.application.dto.response.DashboardResponse;
import co.edu.uniquindio.proyecto.application.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.proyecto.application.dto.response.SolicitudResponse;
import co.edu.uniquindio.proyecto.application.usecase.*;
import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.PaginaResponse;
import co.edu.uniquindio.proyecto.infrastructure.rest.mapper.SolicitudMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST encargado de la gestión del ciclo de vida de las solicitudes académicas.
 *
 * <p>Expone endpoints para la creación, consulta, actualización de estado y generación
 * de reportes relacionados con las solicitudes. Actúa como punto de entrada de la capa
 * de presentación, delegando la lógica de negocio a los casos de uso definidos en la
 * capa de aplicación.</p>
 *
 * <p>Este controlador implementa operaciones como:</p>
 * <ul>
 *     <li>Registro de solicitudes</li>
 *     <li>Consulta y filtrado con paginación</li>
 *     <li>Transiciones de estado (clasificar, revisar, atender, rechazar, cerrar, cancelar)</li>
 *     <li>Consultas específicas del usuario</li>
 *     <li>Generación de reportes y dashboard</li>
 * </ul>
 *
 * <p>La transformación entre entidades de dominio y respuestas REST se realiza mediante
 * {@link SolicitudMapper}.</p>
 */
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
            description = "Lista solicitudes con filtros opcionales y paginación."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada de solicitudes")
    public ResponseEntity<PaginaResponse<SolicitudResponse>> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) String solicitanteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy) {

        Page<Solicitud> resultado = consultarPorEstadoUseCase.ejecutarPaginado(
                estado, tipo, prioridad, solicitanteId, page, size, sortBy
        );

        PaginaResponse<SolicitudResponse> response = new PaginaResponse<>(
                mapper.toResponseList(resultado.getContent()),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.isFirst(),
                resultado.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reporte/por-estado")
    @Operation(
            summary = "Reporte de solicitudes por estado",
            description = "Retorna la cantidad de solicitudes agrupadas por estado."
    )
    @ApiResponse(responseCode = "200", description = "Reporte generado")
    public ResponseEntity<Map<String, Long>> reportePorEstado() {
        return ResponseEntity.ok(consultarPorEstadoUseCase.reportePorEstado());
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

    @GetMapping("/mis-solicitudes")
    @Operation(summary = "Mis solicitudes", description = "Solicitudes del solicitante autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResponse>> misSolicitudes(
            @RequestParam String solicitanteId) {
        List<SolicitudResponse> response = solicitudRepository.findBySolicitanteId(solicitanteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/asignadas-a-mi")
    @Operation(summary = "Solicitudes asignadas", description = "Solicitudes donde soy responsable")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResponse>> asignadasAMi(
            @RequestParam String responsableId) {
        List<SolicitudResponse> response = consultarPorEstadoUseCase
                .ejecutarAsignadasA(responsableId)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Solicitudes pendientes sin responsable")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResponse>> pendientesSinResponsable() {
        List<SolicitudResponse> response = consultarPorEstadoUseCase
                .ejecutarPendientesSinResponsable()
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Solicitudes vencidas", description = "Solicitudes sin resolver después de N días")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResponse>> vencidas(
            @RequestParam(defaultValue = "7") int dias) {
        List<SolicitudResponse> response = consultarPorEstadoUseCase
                .ejecutarVencidas(dias)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reporte/por-tipo")
    @Operation(summary = "Reporte de solicitudes por tipo")
    @ApiResponse(responseCode = "200", description = "Reporte generado")
    public ResponseEntity<Map<String, Long>> reportePorTipo() {
        return ResponseEntity.ok(consultarPorEstadoUseCase.reportePorTipo());
    }

    @GetMapping("/reporte/por-responsable")
    @Operation(summary = "Reporte de solicitudes por responsable")
    @ApiResponse(responseCode = "200", description = "Reporte generado")
    public ResponseEntity<Map<String, Long>> reportePorResponsable() {
        return ResponseEntity.ok(consultarPorEstadoUseCase.reportePorResponsable());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard general del sistema")
    @ApiResponse(responseCode = "200", description = "Resumen del sistema")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(consultarPorEstadoUseCase.dashboard());
    }
}