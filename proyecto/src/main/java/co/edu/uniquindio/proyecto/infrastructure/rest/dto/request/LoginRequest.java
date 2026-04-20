package co.edu.uniquindio.proyecto.infrastructure.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada utilizado para la autenticación de usuarios.
 *
 * <p>Contiene las credenciales necesarias para iniciar sesión en el sistema.
 * Ambos campos son obligatorios y deben ser proporcionados en la solicitud.</p>
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}