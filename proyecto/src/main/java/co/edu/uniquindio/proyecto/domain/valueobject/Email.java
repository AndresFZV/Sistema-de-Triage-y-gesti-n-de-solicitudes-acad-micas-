package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;

public record Email(String valor) {

    public Email {
        if (valor == null || valor.isBlank())
            throw new ReglaDominioException("El email no puede estar vacío");
        if (!valor.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new ReglaDominioException("El email no tiene un formato válido");
    }
}