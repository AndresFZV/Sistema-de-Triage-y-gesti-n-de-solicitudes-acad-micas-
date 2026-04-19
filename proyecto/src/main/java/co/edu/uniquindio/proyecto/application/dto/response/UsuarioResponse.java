package co.edu.uniquindio.proyecto.application.dto.response;

public record UsuarioResponse(
        String id,
        String nombre,
        String email,
        String tipoUsuario
) {}