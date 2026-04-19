package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario ejecutar(String nombre, String email, TipoUsuario tipoUsuario) {
        Usuario usuario = new Usuario(
                java.util.UUID.randomUUID().toString(),
                nombre,
                new Email(email),
                tipoUsuario
        );
        return usuarioRepository.save(usuario);
    }
}