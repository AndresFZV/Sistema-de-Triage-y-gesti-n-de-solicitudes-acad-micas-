package co.edu.uniquindio.proyecto.aplication.dto.response;

public record UsuarioResponse(
        String id,
        String nombre,
        String email,
        String tipoUsuario
) {}