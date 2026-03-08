package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Email emailValido;

    @BeforeEach
    void setUp() {
        emailValido = new Email("estudiante@uniquindio.edu.co");
    }

    @Test
    void debeCrearUsuarioCorrectamente() {
        Usuario usuario = new Usuario("U-001", "Juan Pérez", emailValido, TipoUsuario.ESTUDIANTE);
        assertEquals("U-001", usuario.getId());
        assertEquals("Juan Pérez", usuario.getNombre());
        assertEquals(emailValido, usuario.getEmail());
        assertEquals(TipoUsuario.ESTUDIANTE, usuario.getTipoUsuario());
    }

    @Test
    void noDebeCrearUsuarioSinId() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("", "Juan Pérez", emailValido, TipoUsuario.ESTUDIANTE)
        );
    }

    @Test
    void noDebeCrearUsuarioSinNombre() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("U-001", "", emailValido, TipoUsuario.ESTUDIANTE)
        );
    }

    @Test
    void noDebeCrearUsuarioSinEmail() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("U-001", "Juan Pérez", null, TipoUsuario.ESTUDIANTE)
        );
    }

    @Test
    void noDebeCrearUsuarioSinTipo() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("U-001", "Juan Pérez", emailValido, null)
        );
    }

    @Test
    void estudianteNoDebeSerAdministrativo() {
        Usuario usuario = new Usuario("U-001", "Juan", emailValido, TipoUsuario.ESTUDIANTE);
        assertFalse(usuario.esAdministrativo());
        assertTrue(usuario.esEstudiante());
    }

    @Test
    void administrativoDebeReconocerseCorrectamente() {
        Usuario usuario = new Usuario("U-002", "Ana",
                new Email("ana@uniquindio.edu.co"), TipoUsuario.ADMINISTRATIVO);
        assertTrue(usuario.esAdministrativo());
        assertFalse(usuario.esEstudiante());
    }
}