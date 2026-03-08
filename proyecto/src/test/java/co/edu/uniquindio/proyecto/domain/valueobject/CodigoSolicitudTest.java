package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Value Object {@link CodigoSolicitud}.
 * Valida la construcción, el formato del código generado e invariantes.
 */
class CodigoSolicitudTest {

    @Test
    /** Verifica que un código con valor válido se crea correctamente. */
    void debeCrearCodigoValido() {
        CodigoSolicitud codigo = new CodigoSolicitud("SOL-001");
        assertEquals("SOL-001", codigo.valor());
    }

    @Test
    /** Verifica que no se puede crear un código vacío. */
    void noDebeCrearCodigoVacio() {
        assertThrows(ReglaDominioException.class, () -> new CodigoSolicitud(""));
    }

    @Test
    /** Verifica que no se puede crear un código nulo. */
    void noDebeCrearCodigoNulo() {
        assertThrows(ReglaDominioException.class, () -> new CodigoSolicitud(null));
    }

    @Test
    /** Verifica que el código generado tiene el prefijo SOL-. */
    void generarDebeProducirCodigoConPrefijoSOL() {
        CodigoSolicitud codigo = CodigoSolicitud.generar();
        assertTrue(codigo.valor().startsWith("SOL-"));
    }

    @Test
    /** Verifica que dos códigos generados en momentos distintos son diferentes. */
    void dosCodigosGeneradosDebenSerDiferentes() throws InterruptedException {
        CodigoSolicitud codigo1 = CodigoSolicitud.generar();
        Thread.sleep(1);
        CodigoSolicitud codigo2 = CodigoSolicitud.generar();
        assertNotEquals(codigo1, codigo2);
    }
}