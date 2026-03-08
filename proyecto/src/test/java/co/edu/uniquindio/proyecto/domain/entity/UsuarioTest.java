package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad {@link Usuario}.
 * Valida la construcción correcta, invariantes y comportamiento de roles.
 */
class UsuarioTest {

    private Email emailValido;

    @BeforeEach
    void setUp() {
        emailValido = new Email("estudiante@uniquindio.edu.co");
    }

    @Test
    /** Verifica que un usuario con datos válidos se crea correctamente. */
    void debeCrearUsuarioCorrectamente() {
        Usuario usuario = new Usuario("U-001", "Juan Pérez", emailValido, TipoUsuario.ESTUDIANTE);
        assertEquals("U-001", usuario.getId());
        assertEquals("Juan Pérez", usuario.getNombre());
        assertEquals(emailValido, usuario.getEmail());
        assertEquals(TipoUsuario.ESTUDIANTE, usuario.getTipoUsuario());
    }

    @Test
    /** Verifica que no se puede crear un usuario sin id. */
    void noDebeCrearUsuarioSinId() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("", "Juan Pérez", emailValido, TipoUsuario.ESTUDIANTE)
        );
    }

    @Test
    /** Verifica que no se puede crear un usuario sin nombre. */
    void noDebeCrearUsuarioSinNombre() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("U-001", "", emailValido, TipoUsuario.ESTUDIANTE)
        );
    }

    @Test
    /** Verifica que no se puede crear un usuario sin email. */
    void noDebeCrearUsuarioSinEmail() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("U-001", "Juan Pérez", null, TipoUsuario.ESTUDIANTE)
        );
    }

    @Test
    /** Verifica que no se puede crear un usuario sin tipo. */
    void noDebeCrearUsuarioSinTipo() {
        assertThrows(ReglaDominioException.class, () ->
                new Usuario("U-001", "Juan Pérez", emailValido, null)
        );
    }

    @Test
    /** Verifica que un estudiante no es reconocido como administrativo. */
    void estudianteNoDebeSerAdministrativo() {
        Usuario usuario = new Usuario("U-001", "Juan", emailValido, TipoUsuario.ESTUDIANTE);
        assertFalse(usuario.esAdministrativo());
        assertTrue(usuario.esEstudiante());
    }

    @Test
    /** Verifica que un administrativo es reconocido correctamente por sus métodos de rol. */
    void administrativoDebeReconocerseCorrectamente() {
        Usuario usuario = new Usuario("U-002", "Ana",
                new Email("ana@uniquindio.edu.co"), TipoUsuario.ADMINISTRATIVO);
        assertTrue(usuario.esAdministrativo());
        assertFalse(usuario.esEstudiante());
    }
}