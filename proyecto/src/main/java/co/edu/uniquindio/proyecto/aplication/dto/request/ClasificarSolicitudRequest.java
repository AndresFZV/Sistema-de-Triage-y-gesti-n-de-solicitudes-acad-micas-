package co.edu.uniquindio.proyecto.aplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClasificarSolicitudRequest(

        @NotNull(message = "El tipo de solicitud es obligatorio")
        String tipoSolicitud,

        @NotBlank(message = "El id del administrativo es obligatorio")
        String adminId
) {}