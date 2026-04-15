package co.edu.uniquindio.proyecto.aplication.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnRevisionRequest(

        @NotBlank(message = "El id del responsable es obligatorio")
        String responsableId
) {}