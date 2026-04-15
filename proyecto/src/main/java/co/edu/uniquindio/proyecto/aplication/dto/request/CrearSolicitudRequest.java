package co.edu.uniquindio.proyecto.aplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearSolicitudRequest(

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 20, max = 1000, message = "La descripción debe tener entre 20 y 1000 caracteres")
        String descripcion,

        @NotBlank(message = "El id del solicitante es obligatorio")
        String solicitanteId
) {}