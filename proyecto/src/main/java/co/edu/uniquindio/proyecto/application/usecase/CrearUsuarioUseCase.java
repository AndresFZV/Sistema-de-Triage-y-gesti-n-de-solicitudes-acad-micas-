package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para registrar un nuevo usuario en el sistema.
 *
 * <p>Genera un identificador único automáticamente y construye
 * la entidad {@code Usuario} con los datos proporcionados.
 * La validación del email es delegada al value object {@code Email}.</p>
 */
@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea y persiste un nuevo usuario en el sistema.
     *
     * @param nombre      Nombre completo del usuario.
     * @param email       Email válido y único del usuario.
     * @param tipoUsuario Rol del usuario en el sistema.
     * @return Usuario creado y persistido con su identificador generado.
     */
    @Transactional
    public Usuario ejecutar(String nombre, String email, TipoUsuario tipoUsuario, String password) {
        Usuario usuario = new Usuario(
                java.util.UUID.randomUUID().toString(),
                nombre,
                new Email(email),
                tipoUsuario
        );
        return usuarioRepository.save(usuario, passwordEncoder.encode(password));
    }
}