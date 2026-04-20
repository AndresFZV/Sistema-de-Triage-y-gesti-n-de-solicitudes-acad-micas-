package co.edu.uniquindio.proyecto.infrastructure.rest.controllers;

import co.edu.uniquindio.proyecto.application.service.SecurityService;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.RefreshTokenRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar la autenticación de usuarios
 * y la administración de tokens de seguridad.
 *
 * <p>Este controlador expone endpoints para:</p>
 * <ul>
 *     <li>Autenticación de usuarios (login)</li>
 *     <li>Renovación de tokens de acceso</li>
 *     <li>Finalización de sesión (logout)</li>
 * </ul>
 *
 * <p>Delega la lógica de negocio al servicio {@link SecurityService}.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y gestión de tokens")
public class SecurityController {

    private final SecurityService securityService;

    /**
     * Permite a un usuario autenticarse en el sistema.
     *
     * <p>Recibe las credenciales del usuario y retorna un token de acceso
     * junto con la información necesaria para futuras solicitudes autenticadas.</p>
     *
     * @param request Objeto que contiene las credenciales de autenticación.
     * @return Respuesta HTTP con el token generado.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }

    /**
     * Permite renovar un token de acceso utilizando un refresh token válido.
     *
     * <p>Este endpoint se utiliza cuando el token de acceso ha expirado,
     * evitando que el usuario tenga que autenticarse nuevamente.</p>
     *
     * @param request Objeto que contiene el refresh token.
     * @return Respuesta HTTP con un nuevo token de acceso.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acceso")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refresh(request));
    }

    /**
     * Permite cerrar la sesión del usuario e invalidar el token de acceso.
     *
     * <p>El token se obtiene del encabezado Authorization de la solicitud.
     * Si el token es válido, se delega su invalidación al servicio de seguridad.</p>
     *
     * @param request Solicitud HTTP que contiene el encabezado Authorization.
     * @return Respuesta HTTP sin contenido (204 No Content).
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión e invalidar token")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            securityService.logout(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }
}