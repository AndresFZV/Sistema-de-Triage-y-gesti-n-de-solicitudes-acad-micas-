package co.edu.uniquindio.proyecto.domain.exception;

public class SolicitudNoEncontradaException extends RuntimeException {

    public SolicitudNoEncontradaException(String codigo) {
        super("No se encontró la solicitud con código: " + codigo);
    }
}