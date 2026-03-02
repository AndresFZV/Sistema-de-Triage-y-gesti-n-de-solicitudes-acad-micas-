package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;

public record CodigoSolicitud(String valor) {

    public CodigoSolicitud {
        if (valor == null || valor.isBlank())
            throw new ReglaDominioException("El código de solicitud no puede estar vacío");
    }

    public static CodigoSolicitud generar() {
        return new CodigoSolicitud("SOL-" + System.currentTimeMillis());
    }
}