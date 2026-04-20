package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para cancelar una solicitud.
 *
 * <p>Solo el solicitante original puede cancelar su propia solicitud.
 * El identificador es usado por el dominio para verificar que quien
 * cancela es el mismo que registró la solicitud.</p>
 *
 * @param solicitanteId Identificador único del solicitante. No puede estar vacío.
 */
public record CancelarRequest(

        @NotBlank(message = "El id del solicitante es obligatorio")
        String solicitanteId
) {}