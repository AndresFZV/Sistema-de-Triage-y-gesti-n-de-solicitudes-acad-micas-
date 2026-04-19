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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y gestión de tokens")
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acceso")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refresh(request));
    }

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