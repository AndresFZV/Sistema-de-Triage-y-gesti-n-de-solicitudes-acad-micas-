package co.edu.uniquindio.proyecto.aplication.dto.response;

import java.time.LocalDateTime;

public record SolicitudResponse(
        String codigo,
        String descripcion,
        String estado,
        String tipoSolicitud,
        String prioridad,
        LocalDateTime fechaCreacion,
        UsuarioResponse solicitante,
        UsuarioResponse responsable
) {}