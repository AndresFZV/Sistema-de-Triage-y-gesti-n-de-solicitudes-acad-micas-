package co.edu.uniquindio.proyecto.infrastructure.rest.dto.response;

import java.util.List;

/**
 * DTO genérico de respuesta para representar resultados paginados.
 *
 * <p>Encapsula la información de una página de resultados, incluyendo
 * los elementos contenidos y los metadatos de paginación como número
 * de página, tamaño, total de elementos y estado de la página.</p>
 *
 * @param <T> Tipo de los elementos contenidos en la página.
 */
public record PaginaResponse<T>(
        List<T> contenido,
        int paginaActual,
        int tamañoPagina,
        long totalElementos,
        int totalPaginas,
        boolean primera,
        boolean ultima
) {}