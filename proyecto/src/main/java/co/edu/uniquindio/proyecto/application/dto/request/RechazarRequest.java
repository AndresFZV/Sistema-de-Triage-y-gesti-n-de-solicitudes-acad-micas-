package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RechazarRequest(

        @NotBlank(message = "El id del administrativo es obligatorio")
        String adminId
) {}