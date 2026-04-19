package co.edu.uniquindio.proyecto.infrastructure.rest.dto;

import java.time.Instant;
import java.util.Collection;

/**
 * DTO de respuesta con el token JWT generado tras un login exitoso.
 */
public record TokenResponse(
        String token,
        String type,
        Instant expireAt,
        Collection<String> roles
) {}