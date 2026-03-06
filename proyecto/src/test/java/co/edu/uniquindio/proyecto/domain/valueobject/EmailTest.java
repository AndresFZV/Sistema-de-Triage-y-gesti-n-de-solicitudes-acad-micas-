package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void debeCrearEmailValido() {
        Email email = new Email("estudiante@uniquindio.edu.co");
        assertEquals("estudiante@uniquindio.edu.co", email.valor());
    }

    @Test
    void dosEmailsConMismoValorDebenSerIguales() {
        Email e1 = new Email("estudiante@uniquindio.edu.co");
        Email e2 = new Email("estudiante@uniquindio.edu.co");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void noDebeCrearEmailSinArroba() {
        assertThrows(ReglaDominioException.class, () -> new Email("correo-invalido"));
    }

    @Test
    void noDebeCrearEmailVacio() {
        assertThrows(ReglaDominioException.class, () -> new Email(""));
    }

    @Test
    void noDebeCrearEmailNulo() {
        assertThrows(ReglaDominioException.class, () -> new Email(null));
    }

    @Test
    void noDebeCrearEmailSinDominio() {
        assertThrows(ReglaDominioException.class, () -> new Email("correo@"));
    }
}