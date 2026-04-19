package co.edu.uniquindio.proyecto.application.service;

import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.RefreshTokenRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.TokenResponse;

public interface SecurityService {
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshTokenRequest request);
    void logout(String token);
}