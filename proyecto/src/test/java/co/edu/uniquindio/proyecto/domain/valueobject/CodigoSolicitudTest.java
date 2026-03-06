package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CodigoSolicitudTest {

    @Test
    void debeCrearCodigoValido() {
        CodigoSolicitud codigo = new CodigoSolicitud("SOL-001");
        assertEquals("SOL-001", codigo.valor());
    }

    @Test
    void noDebeCrearCodigoVacio() {
        assertThrows(ReglaDominioException.class, () -> new CodigoSolicitud(""));
    }

    @Test
    void noDebeCrearCodigoNulo() {
        assertThrows(ReglaDominioException.class, () -> new CodigoSolicitud(null));
    }

    @Test
    void generarDebeProducirCodigoConPrefijoSOL() {
        CodigoSolicitud codigo = CodigoSolicitud.generar();
        assertTrue(codigo.valor().startsWith("SOL-"));
    }

    @Test
    void dosCodigosGeneradosDebenSerDiferentes() throws InterruptedException {
        CodigoSolicitud codigo1 = CodigoSolicitud.generar();
        Thread.sleep(1);
        CodigoSolicitud codigo2 = CodigoSolicitud.generar();
        assertNotEquals(codigo1, codigo2);
    }
}