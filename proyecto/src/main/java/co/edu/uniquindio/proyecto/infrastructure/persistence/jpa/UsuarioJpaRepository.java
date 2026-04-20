package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoUsuarioEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa {@link UsuarioRepository}.
 *
 * <p>Actúa como puente entre la capa de dominio y la base de datos relacional.
 * Traduce entre objetos de dominio ({@link Usuario}) y entidades JPA
 * ({@link UsuarioEntity}) usando {@link UsuarioPersistenceMapper}.</p>
 *
 * <p>Al guardar un usuario existente, preserva el identificador interno
 * ({@code Long id}) y los campos de seguridad ({@code password},
 * {@code rolSeguridad}) que no forman parte del modelo de dominio.</p>
 */
@Repository
@RequiredArgsConstructor
public class UsuarioJpaRepository implements UsuarioRepository {

    private final UsuarioJpaDataRepository dataRepository;
    private final UsuarioPersistenceMapper mapper;

    /**
     * Persiste o actualiza un usuario en la base de datos.
     *
     * <p>Si el usuario ya existe (por {@code codigoExterno}), actualiza sus campos
     * preservando el id interno y los campos de seguridad. Si es nuevo, lo crea
     * con valores por defecto para seguridad.</p>
     *
     * @param usuario Entidad de dominio a persistir.
     * @return Usuario guardado mapeado de vuelta al dominio.
     */
    @Override
    @Transactional
    public Usuario save(Usuario usuario, String passwordEncriptado) {
        Optional<UsuarioEntity> existente = dataRepository.findByCodigoExterno(usuario.getId());

        UsuarioEntity entity;
        if (existente.isPresent()) {
            entity = existente.get();
            entity.setNombre(usuario.getNombre());
            entity.setEmail(usuario.getEmail().valor());
            entity.setTipoUsuario(TipoUsuarioEnum.valueOf(usuario.getTipoUsuario().name()));
            if (passwordEncriptado != null) {
                entity.setPassword(passwordEncriptado);
            }
        } else {
            entity = mapper.toEntity(usuario);
            entity.setPassword(passwordEncriptado != null ? passwordEncriptado : "{noop}sin-password");
            entity.setRolSeguridad(RolSeguridadEnum.USER);
        }

        UsuarioEntity saved = dataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * Busca un usuario por su identificador externo de negocio.
     *
     * @param id Identificador externo del usuario.
     * @return Optional con el usuario si existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(String id) {
        return dataRepository.findByCodigoExterno(id)
                .map(mapper::toDomain);
    }

    /**
     * Retorna todos los usuarios registrados en el sistema.
     *
     * @return Lista completa de usuarios.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return dataRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Elimina el usuario con el identificador externo indicado.
     *
     * @param id Identificador externo del usuario a eliminar.
     */
    @Override
    @Transactional
    public void deleteById(String id) {
        dataRepository.findByCodigoExterno(id)
                .ifPresent(dataRepository::delete);
    }

    /**
     * Busca un usuario por su dirección de email.
     *
     * @param email Email del usuario a buscar.
     * @return Optional con el usuario si existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return dataRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    /**
     * Busca usuarios cuyo nombre contenga el texto indicado.
     *
     * @param nombre Texto parcial a buscar en el nombre.
     * @return Lista de usuarios que coinciden con el criterio.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByNombreContaining(String nombre) {
        return dataRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}