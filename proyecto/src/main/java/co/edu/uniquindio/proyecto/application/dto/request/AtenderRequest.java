package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para marcar una solicitud como atendida.
 *
 * <p>Contiene el identificador del administrativo que ejecuta la acción,
 * necesario para validar que quien atiende tiene el rol correspondiente.</p>
 *
 * @param adminId Identificador único del administrativo. No puede estar vacío.
 */
public record AtenderRequest(

        @NotBlank(message = "El id del administrativo es obligatorio")
        String adminId
) {}