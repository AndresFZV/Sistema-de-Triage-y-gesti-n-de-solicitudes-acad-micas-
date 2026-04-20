package co.edu.uniquindio.proyecto.infrastructure.rest.dto.response;

import java.time.Instant;
import java.util.Collection;

/**
 * DTO de respuesta que representa la información de autenticación
 * generada tras un proceso exitoso de login o renovación de token.
 *
 * <p>Incluye el token de acceso, el refresh token, el tipo de token,
 * la fecha de expiración y los roles asociados al usuario autenticado.</p>
 */
public record TokenResponse(
        String token,
        String refreshToken,
        String type,
        Instant expireAt,
        Collection<String> roles
) {}