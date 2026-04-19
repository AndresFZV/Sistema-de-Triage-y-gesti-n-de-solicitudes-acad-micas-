package co.edu.uniquindio.proyecto.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el login.
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}