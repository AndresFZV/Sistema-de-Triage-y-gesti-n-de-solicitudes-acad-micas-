package co.edu.uniquindio.proyecto.infrastructure.rest.controllers;

import co.edu.uniquindio.proyecto.application.dto.request.ActualizarUsuarioRequest;
import co.edu.uniquindio.proyecto.application.dto.request.CambiarPasswordRequest;
import co.edu.uniquindio.proyecto.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.proyecto.application.dto.response.SolicitudResponse;
import co.edu.uniquindio.proyecto.application.dto.response.UsuarioResponse;
import co.edu.uniquindio.proyecto.application.usecase.*;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
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

/**
 * Controlador REST encargado de la gestión de usuarios del sistema.
 *
 * <p>Expone endpoints para la creación, consulta, actualización, eliminación
 * y búsqueda de usuarios. También permite consultar las solicitudes asociadas
 * a un usuario específico.</p>
 *
 * <p>La lógica de negocio es delegada a los casos de uso de la capa de aplicación,
 * manteniendo la separación de responsabilidades conforme a la arquitectura
 * basada en capas.</p>
 *
 * <p>La transformación entre entidades de dominio y respuestas REST se realiza
 * mediante {@link SolicitudMapper}.</p>
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ConsultarUsuariosUseCase consultarUsuariosUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final BuscarUsuariosUseCase buscarUsuariosUseCase;
    private final SolicitudMapper mapper;

    @PostMapping
    @Operation(summary = "Crear usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Email ya registrado")
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody CrearUsuarioRequest request) {

        Usuario usuario = crearUsuarioUseCase.ejecutar(
                request.nombre(),
                request.email(),
                TipoUsuario.valueOf(request.tipoUsuario())
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toUsuarioResponse(usuario));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> response = consultarUsuariosUseCase.ejecutar()
                .stream()
                .map(mapper::toUsuarioResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios por email o nombre")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nombre) {

        if (email != null) {
            return ResponseEntity.ok(
                    mapper.toUsuarioResponse(buscarUsuariosUseCase.porEmail(email))
            );
        }
        if (nombre != null) {
            return ResponseEntity.ok(
                    buscarUsuariosUseCase.porNombre(nombre)
                            .stream()
                            .map(mapper::toUsuarioResponse)
                            .toList()
            );
        }
        return ResponseEntity.badRequest().body("Debe proporcionar email o nombre");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable String id) {
        Usuario usuario = consultarUsuariosUseCase.ejecutarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        return ResponseEntity.ok(mapper.toUsuarioResponse(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "409", description = "Email ya registrado")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {

        Usuario usuario = actualizarUsuarioUseCase.ejecutar(
                id,
                request.nombre(),
                request.email(),
                TipoUsuario.valueOf(request.tipoUsuario())
        );
        return ResponseEntity.ok(mapper.toUsuarioResponse(usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        eliminarUsuarioUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/solicitudes")
    @Operation(summary = "Obtener solicitudes de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes del usuario")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<List<SolicitudResponse>> solicitudesDeUsuario(
            @PathVariable String id) {
        List<SolicitudResponse> response = consultarUsuariosUseCase
                .ejecutarSolicitudesPorUsuario(id)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}