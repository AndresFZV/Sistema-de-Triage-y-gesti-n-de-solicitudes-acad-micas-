package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;

/**
 * Value Object que representa el identificador único de una solicitud.
 *
 * <p>Sigue el formato {@code SOL-{timestamp}}. Es generado automáticamente
 * al momento de registrar una solicitud mediante el método factory {@link #generar()}.</p>
 *
 * <p>Es inmutable y se auto-valida en construcción.</p>
 *
 * @param valor El código de la solicitud. No puede ser nulo ni vacío.
 */
public record CodigoSolicitud(String valor) {

    /**
     * Constructor compacto que valida el valor del código.
     *
     * @throws ReglaDominioException si el valor es nulo o vacío.
     */
    public CodigoSolicitud {
        if (valor == null || valor.isBlank())
            throw new ReglaDominioException("El código de solicitud no puede estar vacío");
    }

    /**
     * Genera un nuevo código único con el prefijo {@code SOL-} seguido
     * del timestamp actual en milisegundos.
     *
     * @return Un nuevo {@code CodigoSolicitud} único.
     */
    public static CodigoSolicitud generar() {
        return new CodigoSolicitud("SOL-" + System.currentTimeMillis());
    }
}