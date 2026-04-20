package co.edu.uniquindio.proyecto.application.dto.response;

/**
 * DTO de respuesta con la información básica de un usuario.
 *
 * <p>Versión resumida de la entidad {@code Usuario}, diseñada para ser
 * embebida en otras respuestas como {@code SolicitudResponse} sin
 * exponer datos sensibles como la contraseña.</p>
 *
 * @param id          Identificador externo único del usuario.
 * @param nombre      Nombre completo del usuario.
 * @param email       Dirección de correo electrónico del usuario.
 * @param tipoUsuario Rol del usuario en el sistema.
 */
public record UsuarioResponse(
        String id,
        String nombre,
        String email,
        String tipoUsuario
) {}