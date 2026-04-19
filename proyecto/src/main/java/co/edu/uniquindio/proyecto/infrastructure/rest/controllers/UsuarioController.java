package co.edu.uniquindio.proyecto.infrastructure.rest.controllers;

import co.edu.uniquindio.proyecto.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.proyecto.application.dto.response.UsuarioResponse;
import co.edu.uniquindio.proyecto.application.usecase.ConsultarUsuariosUseCase;
import co.edu.uniquindio.proyecto.application.usecase.CrearUsuarioUseCase;
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

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ConsultarUsuariosUseCase consultarUsuariosUseCase;
    private final SolicitudMapper mapper;

    @PostMapping
    @Operation(summary = "Crear usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
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

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable String id) {
        Usuario usuario = consultarUsuariosUseCase.ejecutarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        return ResponseEntity.ok(mapper.toUsuarioResponse(usuario));
    }
}