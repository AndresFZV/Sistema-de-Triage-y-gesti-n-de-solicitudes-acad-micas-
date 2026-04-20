package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para cambiar la contraseña de un usuario autenticado.
 *
 * <p>Requiere la contraseña actual para verificar la identidad del usuario
 * antes de aplicar el cambio, evitando modificaciones no autorizadas.</p>
 *
 * @param passwordActual Contraseña actual del usuario. No puede estar vacía.
 * @param passwordNueva  Nueva contraseña deseada. Debe tener al menos 8 caracteres.
 */
public record CambiarPasswordRequest(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
        String passwordNueva
) {}