package co.edu.uniquindio.proyecto.domain.exception;

/**
 * Excepción de dominio lanzada cuando se viola una regla de negocio.
 *
 * <p>Reemplaza el uso genérico de {@link IllegalArgumentException} e
 * {@link IllegalStateException} en todo el dominio, haciendo explícito
 * que el error proviene de una violación de las reglas del negocio
 * y no de un error técnico.</p>
 *
 * <p>Es una {@link RuntimeException} para no forzar el manejo obligatorio
 * en capas superiores, aunque se recomienda capturarla en la capa de
 * aplicación para retornar respuestas adecuadas al cliente.</p>
 */
public class ReglaDominioException extends RuntimeException {

    /**
     * Crea una nueva excepción de dominio con el mensaje descriptivo de la regla violada.
     *
     * @param mensaje Descripción clara de qué regla de negocio fue violada.
     */
    public ReglaDominioException(String mensaje) {
        super(mensaje);
    }
}