package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByCodigoExterno(String codigoExterno);

    Optional<UsuarioEntity> findByEmail(String email);

    List<UsuarioEntity> findByNombreContainingIgnoreCase(String nombre);
}