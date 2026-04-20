package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para cerrar definitivamente una solicitud.
 *
 * <p>Solo un administrativo puede cerrar una solicitud. El cierre
 * es posible únicamente desde los estados ATENDIDA o RECHAZADA.</p>
 *
 * @param adminId Identificador único del administrativo. No puede estar vacío.
 */
public record CerrarRequest(

        @NotBlank(message = "El id del administrativo es obligatorio")
        String adminId
) {}