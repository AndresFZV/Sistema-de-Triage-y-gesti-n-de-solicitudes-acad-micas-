package co.edu.uniquindio.proyecto.aplication.dto.response;

public record SolicitudResponse(
        String codigo,
        String descripcion,
        String estado,
        String solicitante
) {}