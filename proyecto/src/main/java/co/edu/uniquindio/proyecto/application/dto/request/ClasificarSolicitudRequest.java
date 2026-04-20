package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para clasificar una solicitud asignándole un tipo.
 *
 * <p>La clasificación solo puede ejecutarla un administrativo sobre una
 * solicitud en estado CLASIFICACION. La prioridad es calculada
 * automáticamente por el sistema con base en el tipo y el tiempo
 * transcurrido desde la creación.</p>
 *
 * @param tipoSolicitud Tipo asignado a la solicitud (ej. HOMOLOGACION, CANCELACION).
 * @param adminId       Identificador del administrativo que ejecuta la acción.
 */
public record ClasificarSolicitudRequest(

        @NotNull(message = "El tipo de solicitud es obligatorio")
        String tipoSolicitud,

        @NotBlank(message = "El id del administrativo es obligatorio")
        String adminId
) {}