package co.edu.uniquindio.proyecto.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String id) {
        super("No se encontró el usuario con id: " + id);
    }
}