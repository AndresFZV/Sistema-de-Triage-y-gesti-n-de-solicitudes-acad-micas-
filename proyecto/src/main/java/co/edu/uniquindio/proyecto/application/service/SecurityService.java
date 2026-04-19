package co.edu.uniquindio.proyecto.application.service;

import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.TokenResponse;

/**
 * Contrato del servicio de autenticación.
 * Vive en application porque es parte de los casos de uso del sistema.
 */
public interface SecurityService {
    TokenResponse login(LoginRequest request);
}