package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelarRequest(

        @NotBlank(message = "El id del solicitante es obligatorio")
        String solicitanteId
) {}