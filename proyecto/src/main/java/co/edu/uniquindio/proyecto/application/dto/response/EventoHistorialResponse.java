package co.edu.uniquindio.proyecto.application.dto.response;

import java.time.LocalDateTime;

public record EventoHistorialResponse(
        String descripcion,
        String estadoResultante,
        String realizadoPor,
        LocalDateTime fecha
) {}