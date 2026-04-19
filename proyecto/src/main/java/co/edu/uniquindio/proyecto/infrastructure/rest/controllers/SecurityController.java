package co.edu.uniquindio.proyecto.infrastructure.rest.controllers;

import co.edu.uniquindio.proyecto.application.service.SecurityService;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone el endpoint de autenticación.
 * La ruta /api/auth/login está marcada como pública en SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }
}