package co.edu.uniquindio.proyecto.application.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa un evento del historial de una solicitud.
 *
 * <p>Cada evento es inmutable y registra una acción relevante ocurrida
 * sobre la solicitud, como un cambio de estado o una asignación.
 * El historial es cronológico y no puede modificarse externamente.</p>
 *
 * @param descripcion      Descripción legible del evento ocurrido.
 * @param estadoResultante Estado al que transitó la solicitud tras el evento.
 * @param realizadoPor     Nombre del usuario que ejecutó la acción.
 * @param fecha            Fecha y hora exacta en que ocurrió el evento.
 */
public record EventoHistorialResponse(
        String descripcion,
        String estadoResultante,
        String realizadoPor,
        LocalDateTime fecha
) {}