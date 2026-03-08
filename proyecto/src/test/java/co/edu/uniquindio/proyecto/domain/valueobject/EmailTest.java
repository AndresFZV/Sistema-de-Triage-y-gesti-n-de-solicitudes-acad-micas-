package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Value Object {@link Email}.
 * Valida la construcción correcta, igualdad por valor e invariantes.
 */
class EmailTest {

    @Test
    /** Verifica que un email con formato válido se crea correctamente. */
    void debeCrearEmailValido() {
        Email email = new Email("estudiante@uniquindio.edu.co");
        assertEquals("estudiante@uniquindio.edu.co", email.valor());
    }

    @Test
    /** Verifica que dos emails con el mismo valor son iguales (igualdad por valor). */
    void dosEmailsConMismoValorDebenSerIguales() {
        Email e1 = new Email("estudiante@uniquindio.edu.co");
        Email e2 = new Email("estudiante@uniquindio.edu.co");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    /** Verifica que no se puede crear un email sin arroba. */
    void noDebeCrearEmailSinArroba() {
        assertThrows(ReglaDominioException.class, () -> new Email("correo-invalido"));
    }

    @Test
    /** Verifica que no se puede crear un email vacío. */
    void noDebeCrearEmailVacio() {
        assertThrows(ReglaDominioException.class, () -> new Email(""));
    }

    @Test
    /** Verifica que no se puede crear un email nulo. */
    void noDebeCrearEmailNulo() {
        assertThrows(ReglaDominioException.class, () -> new Email(null));
    }

    @Test
    /** Verifica que no se puede crear un email sin dominio. */
    void noDebeCrearEmailSinDominio() {
        assertThrows(ReglaDominioException.class, () -> new Email("correo@"));
    }
}