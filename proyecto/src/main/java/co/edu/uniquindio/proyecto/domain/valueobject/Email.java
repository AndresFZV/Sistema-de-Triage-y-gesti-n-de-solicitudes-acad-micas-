package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;

/**
 * Value Object que representa la dirección de correo electrónico de un usuario.
 *
 * <p>Se define únicamente por su valor. Dos instancias de {@code Email}
 * con el mismo valor son iguales, independientemente de cuándo fueron creadas.</p>
 *
 * <p>Es inmutable y se auto-valida en construcción: no puede existir un
 * {@code Email} con formato inválido.</p>
 *
 * @param valor La dirección de correo electrónico. Debe cumplir el formato
 *              {@code usuario@dominio.extension}.
 */
public record Email(String valor) {

    /**
     * Constructor compacto que valida el formato del email.
     *
     * @throws ReglaDominioException si el valor es nulo, vacío o no tiene formato válido.
     */
    public Email {
        if (valor == null || valor.isBlank())
            throw new ReglaDominioException("El email no puede estar vacío");
        if (!valor.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new ReglaDominioException("El email no tiene un formato válido");
    }
}