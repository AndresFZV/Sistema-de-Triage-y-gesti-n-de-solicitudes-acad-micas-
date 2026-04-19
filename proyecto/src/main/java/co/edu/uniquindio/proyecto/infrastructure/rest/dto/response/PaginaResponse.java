package co.edu.uniquindio.proyecto.infrastructure.rest.dto.response;

import java.util.List;

public record PaginaResponse<T>(
        List<T> contenido,
        int paginaActual,
        int tamañoPagina,
        long totalElementos,
        int totalPaginas,
        boolean primera,
        boolean ultima
) {}