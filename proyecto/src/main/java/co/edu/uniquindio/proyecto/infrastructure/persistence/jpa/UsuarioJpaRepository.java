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

@Repository
@RequiredArgsConstructor
public class UsuarioJpaRepository implements UsuarioRepository {

    private final UsuarioJpaDataRepository dataRepository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        // Busca la entidad existente por codigoExterno
        Optional<UsuarioEntity> existente = dataRepository.findByCodigoExterno(usuario.getId());

        UsuarioEntity entity;
        if (existente.isPresent()) {
            // Actualiza los campos de la entidad existente preservando el id Long
            entity = existente.get();
            entity.setNombre(usuario.getNombre());
            entity.setEmail(usuario.getEmail().valor());
            entity.setTipoUsuario(TipoUsuarioEnum.valueOf(usuario.getTipoUsuario().name()));
        } else {
            // Es un nuevo usuario
            entity = mapper.toEntity(usuario);
            entity.setPassword("{noop}sin-password");
            entity.setRolSeguridad(RolSeguridadEnum.USER);
        }

        UsuarioEntity saved = dataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(String id) {
        return dataRepository.findByCodigoExterno(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return dataRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        dataRepository.findByCodigoExterno(id)
                .ifPresent(entity -> dataRepository.delete(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return dataRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByNombreContaining(String nombre) {
        return dataRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}