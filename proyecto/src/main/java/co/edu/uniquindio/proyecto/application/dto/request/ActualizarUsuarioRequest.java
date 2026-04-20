package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para actualizar los datos de un usuario existente.
 *
 * <p>Todos los campos son obligatorios. Se utiliza en el endpoint
 * de actualización de usuarios y es validado automáticamente por
 * Bean Validation antes de llegar al caso de uso.</p>
 *
 * @param nombre      Nombre completo del usuario. No puede estar vacío.
 * @param email       Dirección de correo electrónico válida. No puede estar vacía.
 * @param tipoUsuario Rol del usuario en el sistema (ej. ESTUDIANTE, ADMINISTRATIVO).
 */
public record ActualizarUsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotNull(message = "El tipo de usuario es obligatorio")
        String tipoUsuario
) {}