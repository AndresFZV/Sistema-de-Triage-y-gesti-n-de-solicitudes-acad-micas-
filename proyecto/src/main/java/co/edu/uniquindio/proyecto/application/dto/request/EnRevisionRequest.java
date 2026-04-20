package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para poner una solicitud en revisión asignando un responsable.
 *
 * <p>Solo un administrativo puede ser asignado como responsable. La solicitud
 * debe estar en estado PENDIENTE para ejecutar esta acción.</p>
 *
 * @param responsableId Identificador único del administrativo responsable.
 */
public record EnRevisionRequest(

        @NotBlank(message = "El id del responsable es obligatorio")
        String responsableId
) {}