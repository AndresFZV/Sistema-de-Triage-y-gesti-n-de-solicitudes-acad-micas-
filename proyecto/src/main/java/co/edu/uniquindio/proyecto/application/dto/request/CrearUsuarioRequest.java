package co.edu.uniquindio.proyecto.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar un nuevo usuario en el sistema.
 *
 * <p>El identificador externo es generado automáticamente. El email
 * debe ser único en el sistema y tener un formato válido.</p>
 *
 * @param nombre      Nombre completo del usuario. No puede estar vacío.
 * @param email       Dirección de correo electrónico válida y única.
 * @param tipoUsuario Rol del usuario en el sistema (ej. ESTUDIANTE, ADMINISTRATIVO).
 */
public record CrearUsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotNull(message = "El tipo de usuario es obligatorio")
        String tipoUsuario,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password
) {}