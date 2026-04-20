package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para rechazar una solicitud durante su revisión.
 *
 * <p>Solo un administrativo puede rechazar una solicitud. La acción
 * solo es válida cuando la solicitud está en estado EN_PROCESO.</p>
 *
 * @param adminId Identificador único del administrativo que ejecuta el rechazo.
 */
public record RechazarRequest(

        @NotBlank(message = "El id del administrativo es obligatorio")
        String adminId
) {}