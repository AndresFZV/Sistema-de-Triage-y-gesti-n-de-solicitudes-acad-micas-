package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar una nueva solicitud académica.
 *
 * <p>Cualquier usuario puede registrar una solicitud. El estado inicial
 * es CLASIFICACION y el código es generado automáticamente por el sistema.</p>
 *
 * @param descripcion   Motivo de la solicitud. Debe tener entre 20 y 1000 caracteres.
 * @param solicitanteId Identificador único del usuario que registra la solicitud.
 */
public record CrearSolicitudRequest(

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 20, max = 1000, message = "La descripción debe tener entre 20 y 1000 caracteres")
        String descripcion,

        @NotBlank(message = "El id del solicitante es obligatorio")
        String solicitanteId
) {}