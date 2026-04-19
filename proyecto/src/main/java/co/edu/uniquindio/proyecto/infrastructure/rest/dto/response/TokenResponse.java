package co.edu.uniquindio.proyecto.infrastructure.rest.dto.response;

import java.time.Instant;
import java.util.Collection;

public record TokenResponse(
        String token,
        String refreshToken,
        String type,
        Instant expireAt,
        Collection<String> roles
) {}